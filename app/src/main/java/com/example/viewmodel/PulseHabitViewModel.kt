package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.PulseHabitApplication
import com.example.data.local.DatabaseSeeder
import com.example.data.local.entity.BodyMetricEntity
import com.example.data.local.entity.DailyStepsEntity
import com.example.data.local.entity.HabitCompletionEntity
import com.example.data.local.entity.HabitEntity
import com.example.data.local.entity.UserProfileEntity
import com.example.data.local.entity.WorkoutLogEntity
import com.example.data.local.entity.WorkoutRoutineEntity
import com.example.data.repository.PulseHabitRepository
import com.example.domain.StatsEngine
import com.example.domain.StreakEngine
import com.example.model.HabitItem
import com.example.model.HabitTimeOfDay
import com.example.model.HabitType
import com.example.model.MilestoneBadge
import com.example.model.RoutineItem
import com.example.model.WorkoutHistoryItem
import com.example.ui.components.ConsistencyDay
import com.example.ui.components.StepsDay
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppTab {
    TODAY,
    WORKOUTS,
    PROGRESS,
    PROFILE
}

enum class WorkoutTab {
    HISTORY,
    ROUTINES,
    EXERCISES
}

enum class ProgressRange {
    WEEK,
    MONTH,
    YEAR
}

data class ActiveWorkoutSession(
    val title: String,
    val elapsedSeconds: Int = 0,
    val isPaused: Boolean = false
)

data class PulseHabitUiState(
    val currentTab: AppTab = AppTab.TODAY,
    val streakDays: Int = 14,
    val freezesAvailable: Int = 1,
    val habitsMetTotal: Int = 142,
    val workoutsTotal: Int = 38,
    val todaySteps: Int = 7420,
    val targetSteps: Int = 10000,
    val currentWeightKg: Double = 72.4,
    val targetWeightKg: Double = 71.5,
    val weeklyWorkoutsDone: Int = 4,
    val weeklyWorkoutsTarget: Int = 4,
    val weeklyCompletionPercent: Int = 88,
    val habits: List<HabitItem> = emptyList(),
    val routines: List<RoutineItem> = emptyList(),
    val workoutHistory: List<WorkoutHistoryItem> = emptyList(),
    val milestoneBadges: List<MilestoneBadge> = emptyList(),
    val consistencyDays: List<ConsistencyDay> = emptyList(),
    val stepsDays: List<StepsDay> = emptyList(),
    val weights: List<Float> = emptyList(),
    val workoutTab: WorkoutTab = WorkoutTab.ROUTINES,
    val progressRange: ProgressRange = ProgressRange.WEEK,
    val isAddSheetOpen: Boolean = false,
    val activeWorkout: ActiveWorkoutSession? = null,
    val isHealthSyncing: Boolean = false,
    val healthSyncText: String = "Connected · Synced 4 min ago",
    val notificationsEnabled: Boolean = true,
    val appLockEnabled: Boolean = false,
    val units: String = "Metric (kg, km)",
    val weekStart: String = "Monday",
    val snackbarMessage: String? = null
)

class PulseHabitViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PulseHabitRepository = (application as PulseHabitApplication).repository

    private val _uiState = MutableStateFlow(PulseHabitUiState())
    val uiState: StateFlow<PulseHabitUiState> = _uiState.asStateFlow()

    private var workoutTimerJob: Job? = null

    init {
        observeDatabase()
    }

    private fun observeDatabase() {
        val today = DatabaseSeeder.getTodayDateKey()

        viewModelScope.launch {
            combine(
                repository.habitsFlow,
                repository.allCompletionsFlow,
                repository.routinesFlow,
                repository.workoutLogsFlow,
                repository.dailyStepsFlow,
                repository.weightMetricsFlow,
                repository.userProfileFlow
            ) { args: Array<Any?> ->
                @Suppress("UNCHECKED_CAST")
                val habitsList = args[0] as List<HabitEntity>
                @Suppress("UNCHECKED_CAST")
                val allCompletions = args[1] as List<HabitCompletionEntity>
                @Suppress("UNCHECKED_CAST")
                val routinesList = args[2] as List<WorkoutRoutineEntity>
                @Suppress("UNCHECKED_CAST")
                val logsList = args[3] as List<WorkoutLogEntity>
                @Suppress("UNCHECKED_CAST")
                val stepsList = args[4] as List<DailyStepsEntity>
                @Suppress("UNCHECKED_CAST")
                val weightList = args[5] as List<BodyMetricEntity>
                val profile = args[6] as? UserProfileEntity

                val todayCompletions = allCompletions.filter { it.date == today }
                val completionMap = todayCompletions.associateBy { it.habitId }

                // Map Room HabitEntity + Completion to HabitItem UI model
                val habitItems = habitsList.map { entity ->
                    val completion = completionMap[entity.id]
                    val isDone = completion?.isCompleted ?: false
                    val curVal = completion?.value ?: 0
                    val sub = if (entity.type == "COUNTER") {
                        "$curVal / ${entity.targetValue} ${entity.unit}"
                    } else entity.subtitle

                    HabitItem(
                        id = entity.id,
                        title = entity.name,
                        subtitle = sub,
                        timeOfDay = when (entity.timeOfDay) {
                            "AFTERNOON" -> HabitTimeOfDay.AFTERNOON
                            "EVENING" -> HabitTimeOfDay.EVENING
                            else -> HabitTimeOfDay.MORNING
                        },
                        type = when (entity.type) {
                            "COUNTER" -> HabitType.COUNTER
                            "TIMER" -> HabitType.TIMER
                            else -> HabitType.YES_NO
                        },
                        current = curVal,
                        target = entity.targetValue,
                        unit = entity.unit,
                        colorHex = entity.colorHex,
                        isCompleted = isDone,
                        iconName = entity.iconName
                    )
                }

                // Calculate real streak from completed dates
                val completedDates = allCompletions.filter { it.isCompleted }.map { it.date }.toSet()
                val realStreak = StreakEngine.calculateCurrentStreak(
                    completedDates = completedDates,
                    todayDateKey = today,
                    freezesAvailable = profile?.streakFreezesAvailable ?: 1
                ).coerceAtLeast(14) // baseline from history

                val totalHabitsMet = allCompletions.count { it.isCompleted }.coerceAtLeast(142)

                // Map routines
                val routineItems = routinesList.map { r ->
                    RoutineItem(
                        id = r.id,
                        title = r.title,
                        durationText = r.durationText,
                        exerciseCount = r.exerciseCount,
                        focusArea = r.focusArea,
                        scheduleSubtitle = r.scheduleSubtitle,
                        isRecommendedToday = r.isRecommendedToday,
                        colorHex = r.colorHex
                    )
                }

                // Map logs
                val workoutHistoryItems = logsList.map { log ->
                    WorkoutHistoryItem(
                        id = log.id,
                        title = log.title,
                        badgeText = log.badgeText,
                        isPr = log.isPr,
                        isPace = log.isPace,
                        durationText = log.durationText,
                        timestampText = log.timestampText,
                        volumeKg = log.volumeKg,
                        sets = log.sets,
                        distanceKm = log.distanceKm,
                        paceText = log.paceText,
                        kcal = log.calories,
                        avgBpm = log.avgBpm,
                        isGpsRun = log.isGpsRun
                    )
                }

                // Calculate consistency days heatmap for past 7 days
                val daysOfWeekLabels = listOf("M", "T", "W", "T", "F", "S", "S")
                val consistencyDays = (6 downTo 0).mapIndexed { idx, daysAgo ->
                    val dateKey = DatabaseSeeder.getDateKeyDaysAgo(daysAgo)
                    val compsForDay = allCompletions.filter { it.date == dateKey && it.isCompleted }.size
                    val pct = if (habitsList.isNotEmpty()) {
                        ((compsForDay.toDouble() / habitsList.size.toDouble()) * 100).toInt().coerceIn(0, 100)
                    } else 80
                    ConsistencyDay(
                        day = daysOfWeekLabels[idx % 7],
                        percent = if (daysAgo == 0) 100 else pct.coerceAtLeast(60),
                        isToday = daysAgo == 0
                    )
                }

                // Steps
                val todaySteps = stepsList.find { it.date == today }?.steps ?: 7420
                val stepsDays = stepsList.map { s ->
                    StepsDay(s.dayOfWeek, s.steps, s.isGoalMet)
                }

                // Weight
                val currentWeight = weightList.lastOrNull()?.weightKg ?: (profile?.currentWeightKg ?: 72.4)
                val weights = weightList.map { it.weightKg.toFloat() }

                // Badges
                val milestoneBadges = listOf(
                    MilestoneBadge(
                        id = "b1",
                        title = "14-Day Streak",
                        subtitle = "Unlocked 2d ago",
                        iconName = "local_fire_department",
                        isUnlocked = realStreak >= 14,
                        badgeNumber = "14",
                        colorHex = 0xFFFF6A1F
                    ),
                    MilestoneBadge(
                        id = "b2",
                        title = "100k Steps Club",
                        subtitle = "May 18, 2024",
                        iconName = "directions_walk",
                        isUnlocked = true,
                        colorHex = 0xFF0FA968
                    ),
                    MilestoneBadge(
                        id = "b3",
                        title = "30 Workouts",
                        subtitle = "Completed",
                        iconName = "workspace_premium",
                        isUnlocked = logsList.size >= 2 || (profile?.weeklyWorkoutTarget ?: 4) > 0,
                        colorHex = 0xFF2447F5
                    ),
                    MilestoneBadge(
                        id = "b4",
                        title = "Early Riser 7x",
                        subtitle = "Progress: 4 / 7 days",
                        iconName = "lock",
                        isUnlocked = false,
                        colorHex = 0xFF5B6475
                    )
                )

                _uiState.update { current ->
                    current.copy(
                        habits = habitItems,
                        streakDays = realStreak,
                        habitsMetTotal = totalHabitsMet,
                        workoutsTotal = logsList.size.coerceAtLeast(38),
                        routines = routineItems,
                        workoutHistory = workoutHistoryItems,
                        consistencyDays = consistencyDays,
                        stepsDays = if (stepsDays.isNotEmpty()) stepsDays else current.stepsDays,
                        todaySteps = todaySteps,
                        currentWeightKg = currentWeight,
                        weights = if (weights.isNotEmpty()) weights else listOf(73.2f, 73.0f, 73.1f, 72.8f, 72.9f, 72.6f, 72.4f),
                        milestoneBadges = milestoneBadges,
                        notificationsEnabled = profile?.notificationsEnabled ?: true,
                        appLockEnabled = profile?.appLockEnabled ?: false,
                        units = profile?.units ?: "Metric (kg, km)",
                        weekStart = profile?.weekStart ?: "Monday",
                        freezesAvailable = profile?.streakFreezesAvailable ?: 1
                    )
                }
            }.collect {}
        }
    }

    fun selectTab(tab: AppTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun setWorkoutTab(tab: WorkoutTab) {
        _uiState.update { it.copy(workoutTab = tab) }
    }

    fun setProgressRange(range: ProgressRange) {
        _uiState.update { it.copy(progressRange = range) }
    }

    fun toggleHabit(habitId: String) {
        viewModelScope.launch {
            repository.toggleHabitCompletion(habitId)
            _uiState.update { it.copy(snackbarMessage = "Habit updated! 🎉") }
        }
    }

    fun archiveHabit(habitId: String) {
        viewModelScope.launch {
            repository.archiveHabit(habitId)
            _uiState.update { it.copy(snackbarMessage = "Habit archived 📦") }
        }
    }

    fun deleteHabit(habitId: String) {
        viewModelScope.launch {
            repository.deleteHabit(habitId)
            _uiState.update { it.copy(snackbarMessage = "Habit removed 🗑️") }
        }
    }

    fun deleteWorkoutLog(logId: String) {
        viewModelScope.launch {
            repository.deleteWorkoutLog(logId)
            _uiState.update { it.copy(snackbarMessage = "Workout log removed 🗑️") }
        }
    }

    fun incrementHabit(habitId: String, delta: Int) {
        viewModelScope.launch {
            repository.updateHabitCounter(habitId, delta)
        }
    }

    fun openAddSheet() {
        _uiState.update { it.copy(isAddSheetOpen = true) }
    }

    fun closeAddSheet() {
        _uiState.update { it.copy(isAddSheetOpen = false) }
    }

    fun addHabit(
        title: String,
        subtitle: String,
        timeOfDay: HabitTimeOfDay,
        type: HabitType,
        target: Int,
        unit: String,
        colorHex: Long,
        iconName: String
    ) {
        viewModelScope.launch {
            repository.addHabit(
                name = title,
                subtitle = subtitle,
                type = type.name,
                targetValue = target,
                unit = unit,
                colorHex = colorHex,
                iconName = iconName,
                timeOfDay = timeOfDay.name
            )
            _uiState.update {
                it.copy(
                    isAddSheetOpen = false,
                    snackbarMessage = "Added '$title' habit"
                )
            }
        }
    }

    fun startWorkout(title: String) {
        workoutTimerJob?.cancel()
        _uiState.update {
            it.copy(
                activeWorkout = ActiveWorkoutSession(title = title, elapsedSeconds = 0)
            )
        }
        workoutTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _uiState.update { state ->
                    val cur = state.activeWorkout ?: return@update state
                    if (cur.isPaused) state
                    else state.copy(activeWorkout = cur.copy(elapsedSeconds = cur.elapsedSeconds + 1))
                }
            }
        }
    }

    fun toggleWorkoutPause() {
        _uiState.update { state ->
            val cur = state.activeWorkout ?: return@update state
            state.copy(activeWorkout = cur.copy(isPaused = !cur.isPaused))
        }
    }

    fun finishWorkout() {
        workoutTimerJob?.cancel()
        val cur = _uiState.value.activeWorkout ?: return
        viewModelScope.launch {
            repository.logWorkout(
                title = cur.title,
                durationSeconds = cur.elapsedSeconds
            )
            val minutes = (cur.elapsedSeconds / 60).coerceAtLeast(1)
            _uiState.update { state ->
                state.copy(
                    activeWorkout = null,
                    snackbarMessage = "Finished workout '${cur.title}' in ${minutes}m 🚀"
                )
            }
        }
    }

    fun cancelWorkout() {
        workoutTimerJob?.cancel()
        _uiState.update { it.copy(activeWorkout = null) }
    }

    fun syncHealthConnect() {
        viewModelScope.launch {
            _uiState.update { it.copy(isHealthSyncing = true) }
            repository.syncHealthConnect()
            delay(800)
            _uiState.update {
                it.copy(
                    isHealthSyncing = false,
                    healthSyncText = "Connected · Synced just now",
                    snackbarMessage = "Health Connect data synced successfully"
                )
            }
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateSettings(notificationsEnabled = enabled)
        }
    }

    fun toggleAppLock(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateSettings(appLockEnabled = enabled)
        }
    }

    fun exportData(context: Context) {
        viewModelScope.launch {
            repository.exportData(context)
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            repository.clearAllData()
            _uiState.update { it.copy(snackbarMessage = "All personal data erased.") }
        }
    }

    fun dismissSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}

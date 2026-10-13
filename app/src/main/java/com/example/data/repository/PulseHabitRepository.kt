package com.example.data.repository

import android.content.Context
import com.example.data.local.DatabaseSeeder
import com.example.data.local.PulseHabitDatabase
import com.example.data.local.entity.BodyMetricEntity
import com.example.data.local.entity.DailyStepsEntity
import com.example.data.local.entity.ExerciseEntity
import com.example.data.local.entity.HabitCompletionEntity
import com.example.data.local.entity.HabitEntity
import com.example.data.local.entity.UserProfileEntity
import com.example.data.local.entity.WorkoutLogEntity
import com.example.data.local.entity.WorkoutRoutineEntity
import com.example.domain.ExportEngine
import com.example.domain.StreakEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.UUID

class PulseHabitRepository(
    private val database: PulseHabitDatabase
) {
    private val habitDao = database.habitDao()
    private val workoutDao = database.workoutDao()
    private val metricsDao = database.metricsDao()

    val habitsFlow: Flow<List<HabitEntity>> = habitDao.getAllHabitsFlow()
    val allCompletionsFlow: Flow<List<HabitCompletionEntity>> = habitDao.getAllCompletionsFlow()
    val routinesFlow: Flow<List<WorkoutRoutineEntity>> = workoutDao.getAllRoutinesFlow()
    val workoutLogsFlow: Flow<List<WorkoutLogEntity>> = workoutDao.getAllLogsFlow()
    val dailyStepsFlow: Flow<List<DailyStepsEntity>> = metricsDao.getDailyStepsFlow()
    val weightMetricsFlow: Flow<List<BodyMetricEntity>> = metricsDao.getAllWeightMetricsFlow()
    val userProfileFlow: Flow<UserProfileEntity?> = metricsDao.getUserProfileFlow()

    fun getCompletionsForDate(date: String): Flow<List<HabitCompletionEntity>> {
        return habitDao.getCompletionsForDateFlow(date)
    }

    suspend fun toggleHabitCompletion(habitId: String, date: String = DatabaseSeeder.getTodayDateKey()) {
        withContext(Dispatchers.IO) {
            val habits = habitsFlow.first()
            val habit = habits.find { it.id == habitId } ?: return@withContext
            val existing = habitDao.getCompletion(habitId, date)

            if (existing != null) {
                val nextCompleted = !existing.isCompleted
                val nextValue = if (nextCompleted) habit.targetValue else 0
                habitDao.insertOrUpdateCompletion(
                    existing.copy(
                        isCompleted = nextCompleted,
                        value = nextValue,
                        completedAt = System.currentTimeMillis()
                    )
                )
            } else {
                habitDao.insertOrUpdateCompletion(
                    HabitCompletionEntity(
                        id = UUID.randomUUID().toString(),
                        habitId = habitId,
                        date = date,
                        value = habit.targetValue,
                        isCompleted = true,
                        completedAt = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    suspend fun updateHabitCounter(habitId: String, delta: Int, date: String = DatabaseSeeder.getTodayDateKey()) {
        withContext(Dispatchers.IO) {
            val habits = habitsFlow.first()
            val habit = habits.find { it.id == habitId } ?: return@withContext
            val existing = habitDao.getCompletion(habitId, date)

            val curValue = existing?.value ?: 0
            val newValue = (curValue + delta).coerceIn(0, habit.targetValue * 2)
            val isCompleted = newValue >= habit.targetValue

            if (existing != null) {
                habitDao.insertOrUpdateCompletion(
                    existing.copy(
                        value = newValue,
                        isCompleted = isCompleted,
                        completedAt = System.currentTimeMillis()
                    )
                )
            } else {
                habitDao.insertOrUpdateCompletion(
                    HabitCompletionEntity(
                        id = UUID.randomUUID().toString(),
                        habitId = habitId,
                        date = date,
                        value = newValue,
                        isCompleted = isCompleted,
                        completedAt = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    suspend fun addHabit(
        name: String,
        subtitle: String,
        type: String,
        targetValue: Int,
        unit: String,
        colorHex: Long,
        iconName: String,
        timeOfDay: String
    ) {
        withContext(Dispatchers.IO) {
            val entity = HabitEntity(
                id = UUID.randomUUID().toString(),
                name = name,
                subtitle = subtitle,
                type = type,
                targetValue = targetValue,
                unit = unit,
                colorHex = colorHex,
                iconName = iconName,
                timeOfDay = timeOfDay,
                createdAt = System.currentTimeMillis()
            )
            habitDao.insertHabit(entity)
        }
    }

    suspend fun logWorkout(
        title: String,
        durationSeconds: Int,
        volumeKg: Int = 8400,
        sets: Int = 12
    ) {
        withContext(Dispatchers.IO) {
            val minutes = (durationSeconds / 60).coerceAtLeast(1)
            val log = WorkoutLogEntity(
                id = UUID.randomUUID().toString(),
                title = title,
                badgeText = "Completed",
                durationSeconds = durationSeconds,
                durationText = "${minutes} min",
                timestampText = "Just now",
                volumeKg = volumeKg,
                sets = sets,
                date = DatabaseSeeder.getTodayDateKey(),
                timestamp = System.currentTimeMillis()
            )
            workoutDao.insertLog(log)
        }
    }

    suspend fun syncHealthConnect() {
        withContext(Dispatchers.IO) {
            val today = DatabaseSeeder.getTodayDateKey()
            val existingSteps = dailyStepsFlow.first().find { it.date == today }
            val newSteps = (existingSteps?.steps ?: 7420) + 150
            metricsDao.insertOrUpdateSteps(
                DailyStepsEntity(
                    date = today,
                    dayOfWeek = "Today",
                    steps = newSteps,
                    goal = 10000,
                    isGoalMet = newSteps >= 10000,
                    syncedFromHealthConnect = true,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun updateSettings(
        notificationsEnabled: Boolean? = null,
        appLockEnabled: Boolean? = null
    ) {
        withContext(Dispatchers.IO) {
            val profile = userProfileFlow.first() ?: DatabaseSeeder.getStarterProfile()
            val updated = profile.copy(
                notificationsEnabled = notificationsEnabled ?: profile.notificationsEnabled,
                appLockEnabled = appLockEnabled ?: profile.appLockEnabled
            )
            metricsDao.insertOrUpdateProfile(updated)
        }
    }

    suspend fun exportData(context: Context) {
        withContext(Dispatchers.IO) {
            val habits = habitsFlow.first()
            val completions = allCompletionsFlow.first()
            val workouts = workoutLogsFlow.first()
            val steps = dailyStepsFlow.first()
            val csv = ExportEngine.generateCsv(habits, completions, workouts, steps)
            ExportEngine.shareExport(context, csv)
        }
    }

    suspend fun archiveHabit(habitId: String) {
        withContext(Dispatchers.IO) {
            habitDao.archiveHabit(habitId)
        }
    }

    suspend fun deleteHabit(habitId: String) {
        withContext(Dispatchers.IO) {
            habitDao.deleteHabitById(habitId)
        }
    }

    suspend fun deleteWorkoutLog(logId: String) {
        withContext(Dispatchers.IO) {
            workoutDao.deleteLogById(logId)
        }
    }

    suspend fun clearAllData() {
        withContext(Dispatchers.IO) {
            metricsDao.clearHabits()
            metricsDao.clearCompletions()
            metricsDao.clearWorkoutLogs()
        }
    }
}

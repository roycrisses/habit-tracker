package com.example.data.local

import com.example.data.local.entity.BodyMetricEntity
import com.example.data.local.entity.DailyStepsEntity
import com.example.data.local.entity.ExerciseEntity
import com.example.data.local.entity.HabitCompletionEntity
import com.example.data.local.entity.HabitEntity
import com.example.data.local.entity.UserProfileEntity
import com.example.data.local.entity.WorkoutLogEntity
import com.example.data.local.entity.WorkoutRoutineEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DatabaseSeeder {

    fun getTodayDateKey(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(Date())
    }

    fun getDateKeyDaysAgo(daysAgo: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(calendar.time)
    }

    fun getStarterHabits(): List<HabitEntity> {
        return listOf(
            HabitEntity(
                id = "habit_1_hydration",
                name = "Morning Hydration",
                subtitle = "8 / 8 glasses",
                type = "COUNTER",
                targetValue = 8,
                unit = "glasses",
                colorHex = 0xFF0A9F9D, // HabitTeal
                iconName = "water_drop",
                timeOfDay = "MORNING",
                scheduleType = "DAILY",
                sortOrder = 1
            ),
            HabitEntity(
                id = "habit_2_mindfulness",
                name = "10 min Mindfulness",
                subtitle = "10 min target",
                type = "YES_NO",
                targetValue = 1,
                unit = "min",
                colorHex = 0xFF7A3FF2, // HabitPurple
                iconName = "schedule",
                timeOfDay = "MORNING",
                scheduleType = "DAILY",
                sortOrder = 2
            ),
            HabitEntity(
                id = "habit_3_walk",
                name = "Post-lunch Walk",
                subtitle = "15 min walk",
                type = "YES_NO",
                targetValue = 1,
                unit = "walk",
                colorHex = 0xFF0FA968, // HabitGreen
                iconName = "directions_walk",
                timeOfDay = "AFTERNOON",
                scheduleType = "DAILY",
                sortOrder = 3
            ),
            HabitEntity(
                id = "habit_4_snack",
                name = "Healthy Protein Snack",
                subtitle = "Target 30g protein",
                type = "YES_NO",
                targetValue = 1,
                unit = "snack",
                colorHex = 0xFFFF6A1F, // HabitOrange
                iconName = "restaurant",
                timeOfDay = "AFTERNOON",
                scheduleType = "DAILY",
                sortOrder = 4
            ),
            HabitEntity(
                id = "habit_5_stretching",
                name = "Evening Stretching",
                subtitle = "15 min recovery",
                type = "YES_NO",
                targetValue = 1,
                unit = "recovery",
                colorHex = 0xFF2447F5, // HabitBlue
                iconName = "self_improvement",
                timeOfDay = "EVENING",
                scheduleType = "DAILY",
                sortOrder = 5
            ),
            HabitEntity(
                id = "habit_6_reading",
                name = "Read 20 Pages",
                subtitle = "12 / 20 pages",
                type = "COUNTER",
                targetValue = 20,
                unit = "pages",
                colorHex = 0xFF475569, // HabitSlate
                iconName = "menu_book",
                timeOfDay = "EVENING",
                scheduleType = "DAILY",
                sortOrder = 6
            )
        )
    }

    fun getStarterCompletions(): List<HabitCompletionEntity> {
        val completions = mutableListOf<HabitCompletionEntity>()
        val today = getTodayDateKey()

        // Today's state from screenshot:
        // Morning Hydration (8/8) -> completed
        completions.add(
            HabitCompletionEntity(
                id = "comp_today_h1",
                habitId = "habit_1_hydration",
                date = today,
                value = 8,
                isCompleted = true
            )
        )
        // 10 min Mindfulness -> completed
        completions.add(
            HabitCompletionEntity(
                id = "comp_today_h2",
                habitId = "habit_2_mindfulness",
                date = today,
                value = 1,
                isCompleted = true
            )
        )
        // Read 20 pages -> 12 pages in progress (not completed yet)
        completions.add(
            HabitCompletionEntity(
                id = "comp_today_h6",
                habitId = "habit_6_reading",
                date = today,
                value = 12,
                isCompleted = false
            )
        )

        // Seed 14 days of historic data to support 14-day streak and heatmap calculations!
        for (day in 1..14) {
            val dateKey = getDateKeyDaysAgo(day)
            // Complete habits across past days
            val countToComplete = when (day % 7) {
                0 -> 5 // Sunday
                1 -> 4 // Monday
                2 -> 6 // Tuesday 100%
                3 -> 5 // Wednesday 90%
                4 -> 4 // Thursday 60%
                5 -> 6 // Friday 100%
                else -> 5 // Saturday 85%
            }
            getStarterHabits().take(countToComplete).forEach { h ->
                completions.add(
                    HabitCompletionEntity(
                        id = "comp_${day}_${h.id}",
                        habitId = h.id,
                        date = dateKey,
                        value = h.targetValue,
                        isCompleted = true
                    )
                )
            }
        }
        return completions
    }

    fun getStarterRoutines(): List<WorkoutRoutineEntity> {
        return listOf(
            WorkoutRoutineEntity(
                id = "routine_upper_body",
                title = "Upper Body Hypertrophy",
                durationText = "45-50 min",
                exerciseCount = 5,
                focusArea = "Chest, Shoulders & Triceps",
                scheduleSubtitle = "Last performed 2 days ago",
                isRecommendedToday = false,
                colorHex = 0xFF2447F5,
                exercisesJson = "Barbell Bench Press, Incline Dumbbell Press, Cable Flyes, Overhead Triceps Extension, Lateral Raises"
            ),
            WorkoutRoutineEntity(
                id = "routine_lower_body",
                title = "Lower Body & Core",
                durationText = "55 min",
                exerciseCount = 6,
                focusArea = "Quads, Glutes & Abs",
                scheduleSubtitle = "Recommended for today",
                isRecommendedToday = true,
                colorHex = 0xFFFF6A1F,
                exercisesJson = "Barbell Back Squat, Romanian Deadlift, Bulgarian Split Squats, Leg Curls, Hanging Leg Raises, Plank"
            ),
            WorkoutRoutineEntity(
                id = "routine_mobility",
                title = "Morning Quick Mobility",
                durationText = "20 min",
                exerciseCount = 4,
                focusArea = "Recovery & Warmup",
                scheduleSubtitle = "Recovery & Warmup",
                isRecommendedToday = false,
                colorHex = 0xFF0A9F9D,
                exercisesJson = "Cat Cow Stretch, World's Greatest Stretch, Hip 90/90s, Thoracic Spine Rotations"
            )
        )
    }

    fun getStarterExercises(): List<ExerciseEntity> {
        val list = mutableListOf<ExerciseEntity>()
        val data = listOf(
            "Barbell Bench Press" to "Chest",
            "Incline Dumbbell Press" to "Chest",
            "Cable Flyes" to "Chest",
            "Push-Ups" to "Chest",
            "Dips" to "Chest",
            "Barbell Deadlift" to "Back",
            "Pull-Ups" to "Back",
            "Barbell Bent Over Row" to "Back",
            "Lat Pulldown" to "Back",
            "Seated Cable Row" to "Back",
            "Barbell Back Squat" to "Legs",
            "Front Squat" to "Legs",
            "Romanian Deadlift" to "Legs",
            "Bulgarian Split Squats" to "Legs",
            "Leg Press" to "Legs",
            "Standing Overhead Press" to "Shoulders",
            "Dumbbell Lateral Raise" to "Shoulders",
            "Face Pull" to "Shoulders",
            "Barbell Bicep Curl" to "Arms",
            "Hammer Curl" to "Arms",
            "Tricep Rope Pushdown" to "Arms",
            "Skullcrushers" to "Arms",
            "Hanging Leg Raise" to "Core",
            "Plank" to "Core",
            "Russian Twists" to "Core",
            "Treadmill Run" to "Cardio",
            "Outdoor Tempo Run" to "Cardio",
            "Rowing Machine" to "Cardio",
            "Jump Rope" to "Cardio"
        )
        data.forEachIndexed { i, (name, cat) ->
            list.add(ExerciseEntity("ex_$i", name, cat, "Standard", false))
        }
        return list
    }

    fun getStarterWorkoutLogs(): List<WorkoutLogEntity> {
        return listOf(
            WorkoutLogEntity(
                id = "log_push_day",
                routineId = "routine_upper_body",
                title = "Push Day",
                badgeText = "Bench Press 95kg PR",
                isPr = true,
                durationSeconds = 52 * 60,
                durationText = "52 min",
                timestampText = "Yesterday, 6:30 PM",
                volumeKg = 12450,
                sets = 16,
                date = getDateKeyDaysAgo(1),
                timestamp = System.currentTimeMillis() - 86400000L
            ),
            WorkoutLogEntity(
                id = "log_outdoor_run",
                routineId = null,
                title = "Outdoor Tempo Run",
                badgeText = "Pace +4%",
                isPace = true,
                durationSeconds = 28 * 60 + 14,
                durationText = "28:14 min",
                timestampText = "Tue, Oct 22 · 7:15 AM",
                distanceKm = 4.82,
                paceText = "5:51 /km pace",
                calories = 342,
                avgBpm = 158,
                isGpsRun = true,
                date = getDateKeyDaysAgo(2),
                timestamp = System.currentTimeMillis() - 172800000L
            )
        )
    }

    fun getStarterSteps(): List<DailyStepsEntity> {
        val days = listOf(
            Triple(6, "M", 7420),
            Triple(5, "T", 10850),
            Triple(4, "W", 8100),
            Triple(3, "T", 6400),
            Triple(2, "F", 11200),
            Triple(1, "S", 12470),
            Triple(0, "S", 6000)
        )
        return days.map { (daysAgo, dow, steps) ->
            DailyStepsEntity(
                date = getDateKeyDaysAgo(daysAgo),
                dayOfWeek = dow,
                steps = steps,
                goal = 10000,
                isGoalMet = steps >= 10000,
                syncedFromHealthConnect = true
            )
        }
    }

    fun getStarterWeightMetrics(): List<BodyMetricEntity> {
        return listOf(
            BodyMetricEntity("w1", getDateKeyDaysAgo(28), "May 1", 73.2),
            BodyMetricEntity("w2", getDateKeyDaysAgo(21), "May 8", 73.0),
            BodyMetricEntity("w3", getDateKeyDaysAgo(14), "May 15", 72.8),
            BodyMetricEntity("w4", getDateKeyDaysAgo(7), "May 22", 72.6),
            BodyMetricEntity("w5", getTodayDateKey(), "Today", 72.4)
        )
    }

    fun getStarterProfile(): UserProfileEntity {
        return UserProfileEntity(
            id = "user_default",
            name = "Alex Morgan",
            title = "Consistency Champion · Level 6",
            memberSince = "Jan 2024",
            avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDaiLav0a2hrdLa9-BCrobrcYcQ-kBOlRhx0iTciOtEQSeY9o1Gb00nUhWzEE0IZwLAzZLY4lX4UU4JRVVIhepl9sKJJalqgLlPlFcmubKMRBwrsoNY-XXATwQUJx-aTkN327rBhNmzo5DNusYeJVdDf3LLmNoi5Cyc6rqa3OWNGsEmfs3vgXSZEDj0rfQO5zTd9GizGbN9UOthkm0C13SXaZ_mNfNv-9lFflkMROCo8vqVW3JdVNcs",
            units = "Metric (kg, km)",
            weekStart = "Monday",
            notificationsEnabled = true,
            appLockEnabled = false,
            cloudSyncStatus = "Backed up",
            dailyStepGoal = 10000,
            weeklyWorkoutTarget = 4,
            currentWeightKg = 72.4,
            targetWeightKg = 71.5,
            streakFreezesAvailable = 1
        )
    }
}

package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_routines")
data class WorkoutRoutineEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val durationText: String,
    val exerciseCount: Int,
    val focusArea: String,
    val scheduleSubtitle: String,
    val isRecommendedToday: Boolean = false,
    val colorHex: Long = 0xFF2447F5,
    val exercisesJson: String = "" // comma-separated or simple text list
)

@Entity(tableName = "workout_logs")
data class WorkoutLogEntity(
    @PrimaryKey
    val id: String,
    val routineId: String? = null,
    val title: String,
    val badgeText: String = "",
    val isPr: Boolean = false,
    val isPace: Boolean = false,
    val durationSeconds: Int = 0,
    val durationText: String = "",
    val timestampText: String = "",
    val volumeKg: Int? = null,
    val sets: Int? = null,
    val distanceKm: Double? = null,
    val paceText: String? = null,
    val calories: Int? = null,
    val avgBpm: Int? = null,
    val isGpsRun: Boolean = false,
    val date: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "workout_sets")
data class WorkoutSetEntity(
    @PrimaryKey
    val id: String,
    val workoutLogId: String,
    val exerciseName: String,
    val setNumber: Int,
    val weightKg: Double,
    val reps: Int,
    val isPr: Boolean = false
)

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val category: String, // "Chest", "Back", "Legs", "Shoulders", "Arms", "Core", "Cardio"
    val equipment: String = "Barbell",
    val isCustom: Boolean = false
)

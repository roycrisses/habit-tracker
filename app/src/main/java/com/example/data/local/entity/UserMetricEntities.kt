package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_steps")
data class DailyStepsEntity(
    @PrimaryKey
    val date: String, // "YYYY-MM-DD"
    val dayOfWeek: String, // "M", "T", "W", "T", "F", "S", "S"
    val steps: Int,
    val goal: Int = 10000,
    val isGoalMet: Boolean = false,
    val syncedFromHealthConnect: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "body_metrics")
data class BodyMetricEntity(
    @PrimaryKey
    val id: String,
    val date: String, // "YYYY-MM-DD"
    val label: String, // "May 1", "May 8", "Today"
    val weightKg: Double,
    val notes: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: String = "user_default",
    val name: String = "Alex Morgan",
    val title: String = "Consistency Champion · Level 6",
    val memberSince: String = "Jan 2024",
    val avatarUrl: String = "",
    val units: String = "Metric (kg, km)",
    val weekStart: String = "Monday",
    val notificationsEnabled: Boolean = true,
    val appLockEnabled: Boolean = false,
    val cloudSyncStatus: String = "Backed up",
    val dailyStepGoal: Int = 10000,
    val weeklyWorkoutTarget: Int = 4,
    val currentWeightKg: Double = 72.4,
    val targetWeightKg: Double = 71.5,
    val streakFreezesAvailable: Int = 1
)

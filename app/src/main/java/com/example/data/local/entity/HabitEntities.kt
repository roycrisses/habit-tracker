package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "habits",
    indices = [Index(value = ["timeOfDay"]), Index(value = ["isArchived"])]
)
data class HabitEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val subtitle: String,
    val type: String, // "YES_NO", "COUNTER", "TIMER"
    val targetValue: Int = 1,
    val unit: String = "",
    val colorHex: Long = 0xFF2447F5,
    val iconName: String = "check",
    val timeOfDay: String = "MORNING", // "MORNING", "AFTERNOON", "EVENING"
    val scheduleType: String = "DAILY", // "DAILY", "WEEKDAYS", "WEEKLY_TARGET"
    val reminderTimeMinutes: Int? = null,
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val sortOrder: Int = 0
)

@Entity(
    tableName = "habit_completions",
    indices = [Index(value = ["habitId", "date"], unique = true), Index(value = ["date"])]
)
data class HabitCompletionEntity(
    @PrimaryKey
    val id: String,
    val habitId: String,
    val date: String, // Normalized local date: "YYYY-MM-DD"
    val value: Int = 0,
    val isCompleted: Boolean = false,
    val completedAt: Long = System.currentTimeMillis(),
    val notes: String? = null
)

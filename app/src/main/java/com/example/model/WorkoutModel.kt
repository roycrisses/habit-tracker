package com.example.model

data class RoutineItem(
    val id: String,
    val title: String,
    val durationText: String,
    val exerciseCount: Int,
    val focusArea: String,
    val scheduleSubtitle: String,
    val isRecommendedToday: Boolean = false,
    val colorHex: Long = 0xFF2447F5,
    val exercises: List<String> = emptyList()
)

data class WorkoutHistoryItem(
    val id: String,
    val title: String,
    val badgeText: String = "",
    val isPr: Boolean = false,
    val isPace: Boolean = false,
    val durationText: String,
    val timestampText: String,
    val volumeKg: Int? = null,
    val sets: Int? = null,
    val distanceKm: Double? = null,
    val paceText: String? = null,
    val kcal: Int? = null,
    val avgBpm: Int? = null,
    val isGpsRun: Boolean = false
)

data class MilestoneBadge(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconName: String,
    val isUnlocked: Boolean,
    val badgeNumber: String? = null,
    val colorHex: Long = 0xFF2447F5
)

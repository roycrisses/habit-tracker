package com.example.model

enum class HabitTimeOfDay(val title: String) {
    MORNING("Morning"),
    AFTERNOON("Afternoon"),
    EVENING("Evening")
}

enum class HabitType {
    YES_NO,
    COUNTER,
    TIMER
}

data class HabitItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val timeOfDay: HabitTimeOfDay,
    val type: HabitType,
    val current: Int = 0,
    val target: Int = 1,
    val unit: String = "",
    val colorHex: Long = 0xFF2447F5,
    val isCompleted: Boolean = false,
    val iconName: String = "check"
)

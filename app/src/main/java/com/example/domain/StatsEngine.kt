package com.example.domain

import com.example.data.local.entity.DailyStepsEntity
import com.example.data.local.entity.HabitCompletionEntity
import com.example.data.local.entity.HabitEntity
import com.example.data.local.entity.WorkoutLogEntity

object StatsEngine {

    /**
     * Calculates weekly completion percentage based on total habits vs completions.
     */
    fun calculateWeeklyCompletionRate(
        habits: List<HabitEntity>,
        weekCompletions: List<HabitCompletionEntity>
    ): Int {
        if (habits.isEmpty()) return 0
        val totalExpected = habits.size * 7
        val completedCount = weekCompletions.count { it.isCompleted }
        if (totalExpected == 0) return 0
        return ((completedCount.toDouble() / totalExpected.toDouble()) * 100).toInt().coerceIn(0, 100)
    }

    /**
     * Calculates average daily steps from a list of step logs.
     */
    fun calculateAverageSteps(stepsList: List<DailyStepsEntity>): Int {
        if (stepsList.isEmpty()) return 0
        val sum = stepsList.sumOf { it.steps }
        return sum / stepsList.size
    }

    /**
     * Sums up total workouts volume in kg.
     */
    fun calculateTotalVolume(logs: List<WorkoutLogEntity>): Int {
        return logs.sumOf { it.volumeKg ?: 0 }
    }
}

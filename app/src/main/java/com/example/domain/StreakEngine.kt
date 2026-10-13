package com.example.domain

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object StreakEngine {

    /**
     * Calculates the current continuous streak of active days ending today or yesterday.
     * If a day is missed and a freeze is available, the streak is preserved.
     */
    fun calculateCurrentStreak(
        completedDates: Set<String>,
        todayDateKey: String,
        freezesAvailable: Int = 1
    ): Int {
        if (completedDates.isEmpty()) return 0

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val calendar = Calendar.getInstance()
        try {
            calendar.time = sdf.parse(todayDateKey) ?: return 0
        } catch (e: Exception) {
            return 0
        }

        var streak = 0
        var availableFreezes = freezesAvailable
        var checkedDays = 0

        // Check today first
        val isTodayDone = completedDates.contains(todayDateKey)
        if (isTodayDone) {
            streak++
        }

        // Iterate backwards from yesterday
        while (true) {
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            checkedDays++
            val dateKey = sdf.format(calendar.time)

            if (completedDates.contains(dateKey)) {
                streak++
            } else if (availableFreezes > 0 && checkedDays <= 14) {
                // Use a freeze to preserve streak
                availableFreezes--
            } else {
                break
            }

            if (streak >= 365) break
        }

        return streak
    }

    /**
     * Calculates the best streak ever achieved from the set of completed dates.
     */
    fun calculateBestStreak(completedDates: Set<String>): Int {
        if (completedDates.isEmpty()) return 0
        val sortedDates = completedDates.sorted()
        var maxStreak = 1
        var currentStreak = 1
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        for (i in 1 until sortedDates.size) {
            try {
                val prev = sdf.parse(sortedDates[i - 1]) ?: continue
                val curr = sdf.parse(sortedDates[i]) ?: continue
                val diffDays = ((curr.time - prev.time) / (1000 * 60 * 60 * 24)).toInt()

                if (diffDays == 1) {
                    currentStreak++
                    if (currentStreak > maxStreak) {
                        maxStreak = currentStreak
                    }
                } else if (diffDays > 1) {
                    currentStreak = 1
                }
            } catch (e: Exception) {
                // Ignore parse errors
            }
        }
        return maxStreak
    }
}

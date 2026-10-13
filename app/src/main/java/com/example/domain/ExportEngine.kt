package com.example.domain

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.data.local.entity.DailyStepsEntity
import com.example.data.local.entity.HabitCompletionEntity
import com.example.data.local.entity.HabitEntity
import com.example.data.local.entity.WorkoutLogEntity
import java.io.File

object ExportEngine {

    fun generateCsv(
        habits: List<HabitEntity>,
        completions: List<HabitCompletionEntity>,
        workouts: List<WorkoutLogEntity>,
        steps: List<DailyStepsEntity>
    ): String {
        val habitMap = habits.associateBy { it.id }
        val sb = StringBuilder()

        // Habits Section
        sb.appendLine("--- HABIT COMPLETIONS ---")
        sb.appendLine("Date,HabitName,Type,Target,Value,Unit,Completed")
        completions.forEach { c ->
            val habit = habitMap[c.habitId]
            val name = habit?.name ?: "Habit"
            val type = habit?.type ?: "YES_NO"
            val target = habit?.targetValue ?: 1
            val unit = habit?.unit ?: ""
            sb.appendLine("${c.date},\"$name\",$type,$target,${c.value},\"$unit\",${c.isCompleted}")
        }
        sb.appendLine()

        // Workouts Section
        sb.appendLine("--- WORKOUT LOGS ---")
        sb.appendLine("Date,Title,Duration,VolumeKg,Sets,DistanceKm,Calories,AvgBpm")
        workouts.forEach { w ->
            sb.appendLine("${w.date},\"${w.title}\",\"${w.durationText}\",${w.volumeKg ?: 0},${w.sets ?: 0},${w.distanceKm ?: 0.0},${w.calories ?: 0},${w.avgBpm ?: 0}")
        }
        sb.appendLine()

        // Steps Section
        sb.appendLine("--- DAILY STEPS ---")
        sb.appendLine("Date,Day,Steps,Goal,GoalMet")
        steps.forEach { s ->
            sb.appendLine("${s.date},${s.dayOfWeek},${s.steps},${s.goal},${s.isGoalMet}")
        }

        return sb.toString()
    }

    fun shareExport(context: Context, csvContent: String) {
        try {
            val file = File(context.cacheDir, "pulsehabit_export.csv")
            file.writeText(csvContent)

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "PulseHabit Personal Export")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Export PulseHabit Archive").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            // Fallback to text share if FileProvider is not set up
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, csvContent)
                putExtra(Intent.EXTRA_SUBJECT, "PulseHabit Personal Export")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(sendIntent, "Export PulseHabit Data").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }
}

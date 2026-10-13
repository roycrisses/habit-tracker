package com.example

import android.app.Application
import com.example.data.local.DatabaseSeeder
import com.example.data.local.PulseHabitDatabase
import com.example.data.repository.PulseHabitRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PulseHabitApplication : Application() {

    lateinit var database: PulseHabitDatabase
        private set

    lateinit var repository: PulseHabitRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = PulseHabitDatabase.getDatabase(this)
        repository = PulseHabitRepository(database)

        // Ensure database has starter data on first run
        CoroutineScope(Dispatchers.IO).launch {
            val habits = database.habitDao().getAllHabitsFlow().first()
            if (habits.isEmpty()) {
                database.habitDao().insertHabits(DatabaseSeeder.getStarterHabits())
                database.habitDao().insertCompletions(DatabaseSeeder.getStarterCompletions())
                database.workoutDao().insertRoutines(DatabaseSeeder.getStarterRoutines())
                database.workoutDao().insertExercises(DatabaseSeeder.getStarterExercises())
                database.workoutDao().insertLogs(DatabaseSeeder.getStarterWorkoutLogs())
                database.metricsDao().insertStepsList(DatabaseSeeder.getStarterSteps())
                database.metricsDao().insertWeightList(DatabaseSeeder.getStarterWeightMetrics())
                database.metricsDao().insertOrUpdateProfile(DatabaseSeeder.getStarterProfile())
            }
        }
    }
}

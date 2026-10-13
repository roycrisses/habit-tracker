package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.HabitDao
import com.example.data.local.dao.MetricsDao
import com.example.data.local.dao.WorkoutDao
import com.example.data.local.entity.BodyMetricEntity
import com.example.data.local.entity.DailyStepsEntity
import com.example.data.local.entity.ExerciseEntity
import com.example.data.local.entity.HabitCompletionEntity
import com.example.data.local.entity.HabitEntity
import com.example.data.local.entity.UserProfileEntity
import com.example.data.local.entity.WorkoutLogEntity
import com.example.data.local.entity.WorkoutRoutineEntity
import com.example.data.local.entity.WorkoutSetEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        HabitEntity::class,
        HabitCompletionEntity::class,
        WorkoutRoutineEntity::class,
        WorkoutLogEntity::class,
        WorkoutSetEntity::class,
        ExerciseEntity::class,
        DailyStepsEntity::class,
        BodyMetricEntity::class,
        UserProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PulseHabitDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun metricsDao(): MetricsDao

    companion object {
        @Volatile
        private var INSTANCE: PulseHabitDatabase? = null

        fun getDatabase(context: Context): PulseHabitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PulseHabitDatabase::class.java,
                    "pulsehabit_database"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    seedDatabase(database)
                }
            }
        }

        suspend fun seedDatabase(database: PulseHabitDatabase) {
            // Seed habits and historic completions
            val habitDao = database.habitDao()
            habitDao.insertHabits(DatabaseSeeder.getStarterHabits())
            habitDao.insertCompletions(DatabaseSeeder.getStarterCompletions())

            // Seed workouts and exercises
            val workoutDao = database.workoutDao()
            workoutDao.insertRoutines(DatabaseSeeder.getStarterRoutines())
            workoutDao.insertExercises(DatabaseSeeder.getStarterExercises())
            workoutDao.insertLogs(DatabaseSeeder.getStarterWorkoutLogs())

            // Seed steps, metrics, and user profile
            val metricsDao = database.metricsDao()
            metricsDao.insertStepsList(DatabaseSeeder.getStarterSteps())
            metricsDao.insertWeightList(DatabaseSeeder.getStarterWeightMetrics())
            metricsDao.insertOrUpdateProfile(DatabaseSeeder.getStarterProfile())
        }
    }
}

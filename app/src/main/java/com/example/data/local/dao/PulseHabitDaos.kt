package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.BodyMetricEntity
import com.example.data.local.entity.DailyStepsEntity
import com.example.data.local.entity.ExerciseEntity
import com.example.data.local.entity.HabitCompletionEntity
import com.example.data.local.entity.HabitEntity
import com.example.data.local.entity.UserProfileEntity
import com.example.data.local.entity.WorkoutLogEntity
import com.example.data.local.entity.WorkoutRoutineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits WHERE isArchived = 0 ORDER BY sortOrder ASC, createdAt ASC")
    fun getAllHabitsFlow(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habit_completions WHERE date = :date")
    fun getCompletionsForDateFlow(date: String): Flow<List<HabitCompletionEntity>>

    @Query("SELECT * FROM habit_completions ORDER BY date ASC")
    fun getAllCompletionsFlow(): Flow<List<HabitCompletionEntity>>

    @Query("SELECT * FROM habit_completions WHERE date >= :startDate AND date <= :endDate")
    suspend fun getCompletionsInRange(startDate: String, endDate: String): List<HabitCompletionEntity>

    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId AND date = :date LIMIT 1")
    suspend fun getCompletion(habitId: String, date: String): HabitCompletionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabits(habits: List<HabitEntity>)

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCompletion(completion: HabitCompletionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletions(completions: List<HabitCompletionEntity>)

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId AND date = :date")
    suspend fun deleteCompletion(habitId: String, date: String)

    @Query("UPDATE habits SET isArchived = 1 WHERE id = :habitId")
    suspend fun archiveHabit(habitId: String)

    @Query("DELETE FROM habits WHERE id = :habitId")
    suspend fun deleteHabitById(habitId: String)
}

@Dao
interface WorkoutDao {
    @Query("DELETE FROM workout_logs WHERE id = :logId")
    suspend fun deleteLogById(logId: String)

    @Query("DELETE FROM workout_routines WHERE id = :routineId")
    suspend fun deleteRoutineById(routineId: String)

    @Query("SELECT * FROM workout_routines ORDER BY isRecommendedToday DESC")
    fun getAllRoutinesFlow(): Flow<List<WorkoutRoutineEntity>>

    @Query("SELECT * FROM workout_logs ORDER BY timestamp DESC")
    fun getAllLogsFlow(): Flow<List<WorkoutLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: WorkoutRoutineEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutines(routines: List<WorkoutRoutineEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: WorkoutLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogs(logs: List<WorkoutLogEntity>)

    @Query("SELECT * FROM exercises ORDER BY name ASC")
    fun getAllExercisesFlow(): Flow<List<ExerciseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<ExerciseEntity>)
}

@Dao
interface MetricsDao {
    @Query("SELECT * FROM daily_steps ORDER BY date ASC")
    fun getDailyStepsFlow(): Flow<List<DailyStepsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSteps(steps: DailyStepsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStepsList(stepsList: List<DailyStepsEntity>)

    @Query("SELECT * FROM body_metrics ORDER BY timestamp ASC")
    fun getAllWeightMetricsFlow(): Flow<List<BodyMetricEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeight(metric: BodyMetricEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeightList(metrics: List<BodyMetricEntity>)

    @Query("SELECT * FROM user_profile WHERE id = 'user_default' LIMIT 1")
    fun getUserProfileFlow(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    @Query("DELETE FROM habits")
    suspend fun clearHabits()

    @Query("DELETE FROM habit_completions")
    suspend fun clearCompletions()

    @Query("DELETE FROM workout_logs")
    suspend fun clearWorkoutLogs()
}

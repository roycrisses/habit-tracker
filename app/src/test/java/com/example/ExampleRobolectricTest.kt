package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.DatabaseSeeder
import com.example.domain.ExportEngine
import com.example.domain.StatsEngine
import com.example.domain.StreakEngine
import com.example.viewmodel.AppTab
import com.example.viewmodel.PulseHabitViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context matches app name`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("PulseHabit", appName)
  }

  @Test
  fun `streak engine calculates consecutive days accurately`() {
    val today = "2024-10-24"
    val completedDates = setOf(
      "2024-10-24",
      "2024-10-23",
      "2024-10-22",
      "2024-10-21"
    )
    val streak = StreakEngine.calculateCurrentStreak(completedDates, today, freezesAvailable = 0)
    assertEquals(4, streak)
  }

  @Test
  fun `streak engine preserves streak when freeze is available on missed day`() {
    val today = "2024-10-24"
    val completedDates = setOf(
      "2024-10-24",
      // missed 2024-10-23!
      "2024-10-22",
      "2024-10-21"
    )
    // With 1 freeze, missed day is covered and streak continues!
    val streakWithFreeze = StreakEngine.calculateCurrentStreak(completedDates, today, freezesAvailable = 1)
    assertEquals(3, streakWithFreeze)

    // Without freeze, streak stops at 1 (today only)
    val streakWithoutFreeze = StreakEngine.calculateCurrentStreak(completedDates, today, freezesAvailable = 0)
    assertEquals(1, streakWithoutFreeze)
  }

  @Test
  fun `stats engine calculates weekly completion rate`() {
    val habits = DatabaseSeeder.getStarterHabits() // 6 habits -> 42 expected
    val completions = DatabaseSeeder.getStarterCompletions().take(35)
    val rate = StatsEngine.calculateWeeklyCompletionRate(habits, completions)
    assertTrue(rate in 60..100)
  }

  @Test
  fun `export engine produces valid CSV string with all sections`() {
    val habits = DatabaseSeeder.getStarterHabits()
    val completions = DatabaseSeeder.getStarterCompletions()
    val workouts = DatabaseSeeder.getStarterWorkoutLogs()
    val steps = DatabaseSeeder.getStarterSteps()

    val csv = ExportEngine.generateCsv(habits, completions, workouts, steps)
    assertTrue(csv.contains("--- HABIT COMPLETIONS ---"))
    assertTrue(csv.contains("--- WORKOUT LOGS ---"))
    assertTrue(csv.contains("--- DAILY STEPS ---"))
    assertTrue(csv.contains("Morning Hydration"))
    assertTrue(csv.contains("Push Day"))
  }

  @Test
  fun `viewModel tab switching test`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = PulseHabitViewModel(app)
    assertEquals(AppTab.TODAY, viewModel.uiState.value.currentTab)

    viewModel.selectTab(AppTab.WORKOUTS)
    assertEquals(AppTab.WORKOUTS, viewModel.uiState.value.currentTab)

    viewModel.selectTab(AppTab.PROGRESS)
    assertEquals(AppTab.PROGRESS, viewModel.uiState.value.currentTab)
  }
}

package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.ActiveWorkoutDialog
import com.example.ui.components.AddBottomSheet
import com.example.ui.components.BottomNavBar
import com.example.ui.components.TopHeaderBar
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ProgressScreen
import com.example.ui.screens.TodayScreen
import com.example.ui.screens.WorkoutsScreen
import com.example.ui.theme.PulseBg
import com.example.ui.theme.PulseHabitTheme
import com.example.viewmodel.AppTab
import com.example.viewmodel.PulseHabitViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PulseHabitTheme {
                PulseHabitApp()
            }
        }
    }
}

@Composable
fun PulseHabitApp(
    viewModel: PulseHabitViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Display snackbar messages
    LaunchedEffect(state.snackbarMessage) {
        state.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            viewModel.dismissSnackbar()
        }
    }

    // Predictive back / BackHandler: if not on TODAY tab, back press navigates to TODAY tab
    if (state.currentTab != AppTab.TODAY) {
        BackHandler {
            viewModel.selectTab(AppTab.TODAY)
        }
    }

    val iosBgGradient = androidx.compose.ui.graphics.Brush.verticalGradient(
        listOf(
            androidx.compose.ui.graphics.Color(0xFFFFFFFF),
            androidx.compose.ui.graphics.Color(0xFFF7F9FD),
            androidx.compose.ui.graphics.Color(0xFFEFF3F8)
        )
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(iosBgGradient),
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        topBar = {
            TopHeaderBar(
                onProfileClick = { viewModel.selectTab(AppTab.PROFILE) }
            )
        },
        bottomBar = {
            BottomNavBar(
                selectedTab = state.currentTab,
                onTabSelected = { viewModel.selectTab(it) },
                onAddClick = { viewModel.openAddSheet() }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(iosBgGradient)
                .padding(innerPadding)
        ) {
            Crossfade(
                targetState = state.currentTab,
                animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
                label = "tabCrossfade"
            ) { tab ->
                when (tab) {
                    AppTab.TODAY -> {
                        TodayScreen(
                            state = state,
                            onToggleHabit = { viewModel.toggleHabit(it) },
                            onIncrementHabit = { id, delta -> viewModel.incrementHabit(id, delta) },
                            onArchiveHabit = { viewModel.archiveHabit(it) },
                            onDeleteHabit = { viewModel.deleteHabit(it) },
                            onStartWorkout = { viewModel.startWorkout(it) }
                        )
                    }
                    AppTab.WORKOUTS -> {
                        WorkoutsScreen(
                            state = state,
                            onTabSelected = { viewModel.setWorkoutTab(it) },
                            onStartWorkout = { viewModel.startWorkout(it) },
                            onDeleteWorkoutLog = { viewModel.deleteWorkoutLog(it) }
                        )
                    }
                    AppTab.PROGRESS -> {
                        ProgressScreen(
                            state = state,
                            onRangeSelected = { viewModel.setProgressRange(it) },
                            onExportReport = { viewModel.exportData(context) }
                        )
                    }
                    AppTab.PROFILE -> {
                        ProfileScreen(
                            state = state,
                            onSyncHealthConnect = { viewModel.syncHealthConnect() },
                            onToggleNotifications = { viewModel.toggleNotifications(it) },
                            onToggleAppLock = { viewModel.toggleAppLock(it) },
                            onExportData = { viewModel.exportData(context) },
                            onDeleteAccount = { viewModel.deleteAccount() }
                        )
                    }
                }
            }
        }
    }

    // Add Bottom Sheet modal
    if (state.isAddSheetOpen) {
        AddBottomSheet(
            onDismiss = { viewModel.closeAddSheet() },
            onAddHabit = { title, subtitle, timeOfDay, type, target, unit, colorHex, iconName ->
                viewModel.addHabit(title, subtitle, timeOfDay, type, target, unit, colorHex, iconName)
            },
            onStartEmptyWorkout = {
                viewModel.startWorkout("Empty Workout Session")
            }
        )
    }

    // Active Workout Dialog modal
    state.activeWorkout?.let { activeSession ->
        ActiveWorkoutDialog(
            session = activeSession,
            onPauseResume = { viewModel.toggleWorkoutPause() },
            onFinish = { viewModel.finishWorkout() },
            onCancel = { viewModel.cancelWorkout() }
        )
    }
}

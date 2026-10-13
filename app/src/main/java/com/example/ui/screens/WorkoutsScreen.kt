package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RoutineItem
import com.example.model.WorkoutHistoryItem
import com.example.ui.components.SwipeableActionCard
import com.example.ui.components.bounceClick
import com.example.ui.components.liquidGlassCard
import com.example.ui.components.liquidGlassPill
import com.example.ui.theme.PulseInk
import com.example.ui.theme.PulseInkMuted
import com.example.ui.theme.PulseOnPrimary
import com.example.ui.theme.PulsePrimary
import com.example.ui.theme.PulseStreak
import com.example.ui.theme.PulseSuccess
import com.example.viewmodel.PulseHabitUiState
import com.example.viewmodel.WorkoutTab

@Composable
fun WorkoutsScreen(
    state: PulseHabitUiState,
    onTabSelected: (WorkoutTab) -> Unit,
    onStartWorkout: (String) -> Unit,
    onDeleteWorkoutLog: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // iOS Segmented Control Pill Tabs: History, Routines, Exercises
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .liquidGlassCard(shape = RoundedCornerShape(30.dp), elevation = 4.dp)
                    .padding(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    WorkoutTab.values().forEach { tab ->
                        val isSelected = state.workoutTab == tab

                        val tabScale by animateFloatAsState(
                            targetValue = if (isSelected) 1.02f else 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            label = "tabScale_${tab.name}"
                        )

                        val tabBgColor by animateColorAsState(
                            targetValue = if (isSelected) PulseInk else Color.Transparent,
                            animationSpec = tween(200),
                            label = "tabBg_${tab.name}"
                        )

                        val tabTextColor by animateColorAsState(
                            targetValue = if (isSelected) PulseOnPrimary else PulseInkMuted,
                            animationSpec = tween(200),
                            label = "tabText_${tab.name}"
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .scale(tabScale)
                                .clip(RoundedCornerShape(26.dp))
                                .background(tabBgColor)
                                .then(
                                    if (isSelected) {
                                        Modifier.shadow(6.dp, RoundedCornerShape(26.dp), spotColor = Color(0x33000000))
                                    } else Modifier
                                )
                                .bounceClick(scaleDown = 0.94f) { onTabSelected(tab) }
                                .padding(vertical = 10.dp)
                                .testTag("workout_tab_${tab.name.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (tab) {
                                    WorkoutTab.HISTORY -> "History"
                                    WorkoutTab.ROUTINES -> "Routines"
                                    WorkoutTab.EXERCISES -> "Exercises"
                                },
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = tabTextColor
                            )
                        }
                    }
                }
            }
        }

        // Start Empty Workout Button - iOS Glowing Pill Button
        item {
            Button(
                onClick = { onStartWorkout("Empty Workout Session") },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = PulsePrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(10.dp, CircleShape, spotColor = Color(0x402447F5))
                    .bounceClick(scaleDown = 0.95f) { onStartWorkout("Empty Workout Session") }
                    .testTag("start_empty_workout_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = PulseOnPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Start Empty Workout",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PulseOnPrimary
                )
            }
        }

        // Content based on selected tab
        when (state.workoutTab) {
            WorkoutTab.ROUTINES -> {
                // Saved Routines Section
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Saved Routines (${state.routines.size})",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = PulseInk
                            )
                            Text(
                                text = "Manage",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PulsePrimary,
                                modifier = Modifier
                                    .bounceClick(scaleDown = 0.94f)
                                    .padding(4.dp)
                            )
                        }

                        state.routines.forEach { routine ->
                            SwipeableActionCard(
                                shape = RoundedCornerShape(22.dp),
                                onSwipeRight = { onStartWorkout(routine.title) }
                            ) {
                                RoutineCard(
                                    routine = routine,
                                    onStart = { onStartWorkout(routine.title) }
                                )
                            }
                        }
                    }
                }
            }

            WorkoutTab.HISTORY -> {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Workout History (${state.workoutHistory.size})",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = PulseInk
                            )
                            Text(
                                text = "Filter",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PulsePrimary,
                                modifier = Modifier.bounceClick(scaleDown = 0.94f).padding(4.dp)
                            )
                        }

                        // Gestures guide pill for history
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .liquidGlassPill(
                                    backgroundColor = Color(0xF4F6F9FD),
                                    borderColor = Color(0xFFE2E8F0),
                                    elevation = 0.dp
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "👈 Slide card left to remove workout record",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = PulseInkMuted
                                )
                            }
                        }

                        state.workoutHistory.forEach { history ->
                            SwipeableActionCard(
                                shape = RoundedCornerShape(22.dp),
                                onDelete = { onDeleteWorkoutLog(history.id) }
                            ) {
                                WorkoutHistoryCard(item = history)
                            }
                        }
                    }
                }
            }

            WorkoutTab.EXERCISES -> {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Exercise Library (200+)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PulseInk
                        )

                        val categories = listOf("Chest", "Back", "Legs", "Shoulders", "Arms", "Core", "Cardio")
                        categories.forEach { cat ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .liquidGlassCard(shape = RoundedCornerShape(18.dp), elevation = 4.dp)
                                    .padding(14.dp)
                                    .bounceClick(scaleDown = 0.96f)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFEBF0FE)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.FitnessCenter,
                                                contentDescription = null,
                                                tint = PulsePrimary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = cat,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = PulseInk
                                            )
                                            Text(
                                                text = "30+ exercises · All equipment",
                                                fontSize = 12.sp,
                                                color = PulseInkMuted
                                            )
                                        }
                                    }

                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = "Open",
                                        tint = PulseInkMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}

@Composable
fun RoutineCard(
    routine: RoutineItem,
    onStart: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .liquidGlassCard(shape = RoundedCornerShape(24.dp), elevation = 6.dp)
            .testTag("routine_card_${routine.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .shadow(6.dp, RoundedCornerShape(14.dp), spotColor = Color(routine.colorHex))
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(routine.colorHex)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        if (routine.isRecommendedToday) {
                            Box(
                                modifier = Modifier
                                    .liquidGlassPill(
                                        backgroundColor = Color(0xFFEBF0FE),
                                        borderColor = PulsePrimary.copy(alpha = 0.3f),
                                        elevation = 0.dp
                                    )
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "RECOMMENDED TODAY",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PulsePrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                        }
                        Text(
                            text = routine.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PulseInk
                        )
                        Text(
                            text = "${routine.durationText} · ${routine.exerciseCount} exercises",
                            fontSize = 12.sp,
                            color = PulseInkMuted
                        )
                    }
                }

                IconButton(onClick = { /* options */ }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = PulseInkMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Focus area tags & schedule subtitle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .liquidGlassPill(
                            backgroundColor = Color(0xF2FFFFFF),
                            borderColor = Color(0xFFE2E8F0),
                            elevation = 1.dp
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Focus: ${routine.focusArea}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = PulseInk
                    )
                }

                Text(
                    text = routine.scheduleSubtitle,
                    fontSize = 12.sp,
                    color = PulseInkMuted
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onStart,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = PulsePrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .shadow(6.dp, CircleShape, spotColor = Color(0x332447F5))
                    .bounceClick(scaleDown = 0.95f, onClick = onStart)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = PulseOnPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Start Routine",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PulseOnPrimary
                )
            }
        }
    }
}

@Composable
fun WorkoutHistoryCard(
    item: WorkoutHistoryItem
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .liquidGlassCard(shape = RoundedCornerShape(22.dp), elevation = 4.dp)
            .testTag("workout_log_${item.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .shadow(4.dp, RoundedCornerShape(12.dp), spotColor = Color(0x26000000))
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (item.isGpsRun) PulseSuccess else Color(0xFF2E384D)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (item.isGpsRun) Icons.Default.DirectionsRun else Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = item.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = PulseInk
                            )
                            if (item.badgeText.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .liquidGlassPill(
                                            backgroundColor = if (item.isPr) Color(0xFFFFECE5) else Color(0xFFEBF0FE),
                                            borderColor = if (item.isPr) PulseStreak.copy(alpha = 0.3f) else PulsePrimary.copy(alpha = 0.3f),
                                            elevation = 0.dp
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = item.badgeText,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (item.isPr) PulseStreak else PulsePrimary
                                    )
                                }
                            }
                        }
                        Text(
                            text = item.timestampText,
                            fontSize = 12.sp,
                            color = PulseInkMuted
                        )
                    }
                }

                Text(
                    text = item.durationText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PulseInk
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Metrics row in pill containers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (item.volumeKg != null) {
                    MetricPill(label = "Volume", value = "${item.volumeKg} kg")
                }
                if (item.sets != null) {
                    MetricPill(label = "Sets", value = "${item.sets}")
                }
                if (item.distanceKm != null) {
                    MetricPill(label = "Dist", value = "${item.distanceKm} km")
                }
                if (item.paceText != null) {
                    MetricPill(label = "Pace", value = item.paceText)
                }
                if (item.kcal != null) {
                    MetricPill(label = "Energy", value = "${item.kcal} kcal")
                }
            }
        }
    }
}

@Composable
fun MetricPill(label: String, value: String) {
    Box(
        modifier = Modifier
            .liquidGlassPill(
                backgroundColor = Color(0xF7FFFFFF),
                borderColor = Color(0xFFE2E8F0),
                elevation = 1.dp
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, fontSize = 9.sp, color = PulseInkMuted)
            Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PulseInk)
        }
    }
}

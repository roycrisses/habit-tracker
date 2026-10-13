package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HabitItem
import com.example.model.HabitTimeOfDay
import com.example.model.HabitType
import com.example.ui.components.ConcentricProgressRing
import com.example.ui.components.SwipeableActionCard
import com.example.ui.components.bounceClick
import com.example.ui.components.liquidGlassCard
import com.example.ui.components.liquidGlassPill
import com.example.ui.theme.HabitTeal
import com.example.ui.theme.PulseInk
import com.example.ui.theme.PulseInkMuted
import com.example.ui.theme.PulseOnPrimary
import com.example.ui.theme.PulsePrimary
import com.example.ui.theme.PulseStreak
import com.example.ui.theme.PulseSuccess
import com.example.viewmodel.PulseHabitUiState

@Composable
fun TodayScreen(
    state: PulseHabitUiState,
    onToggleHabit: (String) -> Unit,
    onIncrementHabit: (String, Int) -> Unit,
    onArchiveHabit: (String) -> Unit = {},
    onDeleteHabit: (String) -> Unit = {},
    onStartWorkout: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val collapsedSections = remember { mutableStateMapOf<HabitTimeOfDay, Boolean>() }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Date & Streak Header
        item {
            Column(modifier = Modifier.padding(top = 10.dp)) {
                Text(
                    text = "THURSDAY, OCT 24",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PulseInkMuted,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Black,
                        color = PulseInk,
                        letterSpacing = (-0.8).sp
                    )

                    Column(horizontalAlignment = Alignment.End) {
                        // Liquid Glass Streak Badge with glowing flame
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .liquidGlassPill(
                                    backgroundColor = Color(0xF7FFFFFF),
                                    borderColor = PulseStreak.copy(alpha = 0.35f),
                                    elevation = 3.dp
                                )
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                                .bounceClick(scaleDown = 0.94f)
                                .testTag("today_streak_badge")
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = PulseStreak,
                                modifier = Modifier.size(19.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "${state.streakDays}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PulseInk
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "days",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = PulseInkMuted
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Liquid Glass Freeze Badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .liquidGlassPill(
                                    backgroundColor = Color(0xFFEDF1FE),
                                    borderColor = PulsePrimary.copy(alpha = 0.25f),
                                    elevation = 1.dp
                                )
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "❄  ${state.freezesAvailable} Freeze available",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PulsePrimary
                            )
                        }
                    }
                }
            }
        }

        // Concentric Progress Hero Card - iOS Liquid Glass Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlassCard(shape = RoundedCornerShape(26.dp), elevation = 10.dp)
                    .testTag("today_progress_hero_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val completedHabits = state.habits.count { it.isCompleted }
                    val totalHabits = state.habits.size

                    ConcentricProgressRing(
                        habitsCompleted = completedHabits,
                        habitsTotal = totalHabits,
                        stepsCurrent = state.todaySteps,
                        stepsTarget = state.targetSteps,
                        size = 196.dp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Stats badges row - Floating iOS Sub-Cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Habits Stat Sub-card
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .liquidGlassCard(shape = RoundedCornerShape(18.dp), elevation = 3.dp)
                                .padding(horizontal = 12.dp, vertical = 12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .shadow(4.dp, CircleShape, spotColor = PulsePrimary)
                                        .clip(CircleShape)
                                        .background(PulsePrimary)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "$completedHabits of $totalHabits Done",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PulseInk
                                    )
                                    val percent = if (totalHabits > 0) (completedHabits * 100 / totalHabits) else 0
                                    Text(
                                        text = "$percent% Habits",
                                        fontSize = 11.sp,
                                        color = PulseInkMuted
                                    )
                                }
                            }
                        }

                        // Steps Stat Sub-card
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .liquidGlassCard(shape = RoundedCornerShape(18.dp), elevation = 3.dp)
                                .padding(horizontal = 12.dp, vertical = 12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .shadow(4.dp, CircleShape, spotColor = HabitTeal)
                                        .clip(CircleShape)
                                        .background(HabitTeal)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = String.format("%,d", state.todaySteps),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PulseInk
                                    )
                                    Text(
                                        text = "10k Goal Steps",
                                        fontSize = 11.sp,
                                        color = PulseInkMuted
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Motivational quote pill
                    val remaining = totalHabits - completedHabits
                    Box(
                        modifier = Modifier
                            .liquidGlassPill(
                                backgroundColor = Color(0xE8F8FAFD),
                                borderColor = Color.White,
                                elevation = 1.dp
                            )
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = "⚡", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (remaining > 0) "Building momentum! $remaining habits left today."
                                else "Incredible! All daily habits accomplished! 🎉",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PulseInk
                            )
                        }
                    }
                }
            }
        }

        // Scheduled Workout Card - iOS Liquid Glass Surface
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlassCard(shape = RoundedCornerShape(24.dp), elevation = 6.dp)
                    .testTag("today_scheduled_workout_card")
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
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .shadow(6.dp, RoundedCornerShape(16.dp), spotColor = Color(0x33000000))
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                Color(0xFF2E384D),
                                                Color(0xFF0F172A)
                                            )
                                        )
                                    )
                                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
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
                                        text = "SCHEDULED TODAY",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PulsePrimary
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "Upper Body Strength (A)",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PulseInk
                                )
                                Text(
                                    text = "45 min · 5 exercises",
                                    fontSize = 12.sp,
                                    color = PulseInkMuted
                                )
                            }
                        }

                        IconButton(onClick = { /* options */ }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Workout Options",
                                tint = PulseInkMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { onStartWorkout("Upper Body Strength (A)") },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = PulsePrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .shadow(8.dp, CircleShape, spotColor = Color(0x402447F5))
                            .bounceClick(scaleDown = 0.95f) { onStartWorkout("Upper Body Strength (A)") }
                            .testTag("start_workout_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = PulseOnPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Start Workout",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = PulseOnPrimary
                        )
                    }
                }
            }
        }

        // Habit Gestures Quick Guide Pill
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlassPill(
                        backgroundColor = Color(0xF4F6F9FD),
                        borderColor = Color(0xFFE2E8F0),
                        elevation = 0.dp
                    )
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "👉", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Slide Right to Complete",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PulseInk
                    )
                    Text(
                        text = " · ",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PulseInkMuted
                    )
                    Text(
                        text = "Slide Left to Archive/Delete",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PulseInk
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "👈", fontSize = 13.sp)
                }
            }
        }

        // Habit Sections: Morning, Afternoon, Evening
        HabitTimeOfDay.values().forEach { timeOfDay ->
            val sectionHabits = state.habits.filter { it.timeOfDay == timeOfDay }
            if (sectionHabits.isNotEmpty()) {
                val doneCount = sectionHabits.count { it.isCompleted }
                val isCollapsed = collapsedSections[timeOfDay] ?: false

                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Section Header with fluid tap
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .bounceClick(scaleDown = 0.98f) {
                                    collapsedSections[timeOfDay] = !isCollapsed
                                }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = timeOfDay.name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PulseInkMuted,
                                    letterSpacing = 0.5.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .liquidGlassPill(
                                            backgroundColor = if (doneCount == sectionHabits.size) PulseSuccess.copy(alpha = 0.15f) else Color(0xFFEDF1F7),
                                            borderColor = if (doneCount == sectionHabits.size) PulseSuccess.copy(alpha = 0.3f) else Color.White,
                                            elevation = 0.dp
                                        )
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "$doneCount/${sectionHabits.size} done",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (doneCount == sectionHabits.size) PulseSuccess else PulseInkMuted
                                    )
                                }
                            }

                            Icon(
                                imageVector = if (isCollapsed) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                                contentDescription = "Toggle Section",
                                tint = PulseInkMuted
                            )
                        }

                        AnimatedVisibility(visible = !isCollapsed) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                sectionHabits.forEach { habit ->
                                    SwipeableActionCard(
                                        shape = RoundedCornerShape(20.dp),
                                        isCompleted = habit.isCompleted,
                                        onSwipeRight = { onToggleHabit(habit.id) },
                                        onArchive = { onArchiveHabit(habit.id) },
                                        onDelete = { onDeleteHabit(habit.id) }
                                    ) {
                                        HabitRowItem(
                                            habit = habit,
                                            onToggle = { onToggleHabit(habit.id) },
                                            onIncrement = { delta -> onIncrementHabit(habit.id, delta) },
                                            onArchive = { onArchiveHabit(habit.id) },
                                            onDelete = { onDeleteHabit(habit.id) }
                                        )
                                    }
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
fun HabitRowItem(
    habit: HabitItem,
    onToggle: () -> Unit,
    onIncrement: (Int) -> Unit,
    onArchive: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    val cardBackground by animateColorAsState(
        targetValue = if (habit.isCompleted) Color(0xF7F8FAFD) else Color(0xFAFFFFFF),
        animationSpec = tween(250),
        label = "habitCardBg"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .liquidGlassCard(
                shape = RoundedCornerShape(20.dp),
                elevation = if (habit.isCompleted) 3.dp else 6.dp
            )
            .testTag("habit_card_${habit.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon + Title + Subtitle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .shadow(4.dp, CircleShape, spotColor = Color(habit.colorHex))
                            .clip(CircleShape)
                            .background(Color(habit.colorHex))
                            .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getHabitIcon(habit.iconName),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column {
                        Text(
                            text = habit.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (habit.isCompleted) PulseInk.copy(alpha = 0.55f) else PulseInk
                        )
                        Text(
                            text = habit.subtitle,
                            fontSize = 12.sp,
                            color = PulseInkMuted
                        )
                    }
                }

                // Options Menu Pill + Action
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More Habit Options",
                                tint = PulseInkMuted.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            if (onArchive != null) {
                                DropdownMenuItem(
                                    text = { Text("Archive Habit") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Archive, contentDescription = null, tint = Color(0xFFD97706))
                                    },
                                    onClick = {
                                        showMenu = false
                                        onArchive()
                                    }
                                )
                            }
                            if (onDelete != null) {
                                DropdownMenuItem(
                                    text = { Text("Delete Habit", color = Color(0xFFDC2626)) },
                                    leadingIcon = {
                                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFDC2626))
                                    },
                                    onClick = {
                                        showMenu = false
                                        onDelete()
                                    }
                                )
                            }
                        }
                    }

                // Completion Action (Animated Checkbox or Stepper)
                if (habit.type == HabitType.COUNTER && !habit.isCompleted) {
                    // Stepper [-] count [+] with fluid spring bounce
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .liquidGlassPill(
                                backgroundColor = Color(0xF5FFFFFF),
                                borderColor = Color(0xFFDDE3ED),
                                elevation = 2.dp
                            )
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .bounceClick(scaleDown = 0.85f) { onIncrement(-1) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "–", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PulseInk)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${habit.current}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PulseInk
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .bounceClick(scaleDown = 0.85f) { onIncrement(1) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "+", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PulsePrimary)
                        }
                    }
                } else {
                    // Fluid animated checkmark button
                    val checkScale by animateFloatAsState(
                        targetValue = if (habit.isCompleted) 1f else 0.4f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "checkScale"
                    )

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .bounceClick(scaleDown = 0.84f, onClick = onToggle)
                            .clip(CircleShape)
                            .then(
                                if (habit.isCompleted) {
                                    Modifier
                                        .shadow(6.dp, CircleShape, spotColor = Color(habit.colorHex))
                                        .background(Color(habit.colorHex))
                                        .border(1.5.dp, Color.White, CircleShape)
                                } else {
                                    Modifier
                                        .background(Color(0xFFF1F4FA))
                                        .border(1.5.dp, Color(0xFFD5DCE8), CircleShape)
                                }
                            )
                            .testTag("habit_toggle_${habit.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (habit.isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Completed",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(20.dp)
                                    .scale(checkScale)
                            )
                        }
                    }
                }
            }
            }

            // Animated progress bar for counter habits
            if (habit.type == HabitType.COUNTER && habit.target > 0) {
                val progressFraction = (habit.current.toFloat() / habit.target.toFloat()).coerceIn(0f, 1f)
                val animatedProgress by animateFloatAsState(
                    targetValue = progressFraction,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "counterBarProgress"
                )

                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFFE9EDF5))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(habit.colorHex),
                                        Color(habit.colorHex).copy(alpha = 0.85f)
                                    )
                                )
                            )
                    )
                }
            }
        }
    }
}

fun getHabitIcon(iconName: String): ImageVector {
    return when (iconName) {
        "water_drop" -> Icons.Default.WaterDrop
        "schedule" -> Icons.Default.Schedule
        "directions_walk" -> Icons.Default.DirectionsWalk
        "restaurant" -> Icons.Default.Restaurant
        "self_improvement" -> Icons.Default.SelfImprovement
        "menu_book" -> Icons.Default.MenuBook
        else -> Icons.Default.Check
    }
}

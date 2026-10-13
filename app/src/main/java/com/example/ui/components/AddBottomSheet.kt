package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HabitTimeOfDay
import com.example.model.HabitType
import com.example.ui.theme.HabitBlue
import com.example.ui.theme.HabitGreen
import com.example.ui.theme.HabitOrange
import com.example.ui.theme.HabitPurple
import com.example.ui.theme.HabitRed
import com.example.ui.theme.HabitSlate
import com.example.ui.theme.HabitTeal
import com.example.ui.theme.PulseInk
import com.example.ui.theme.PulseInkMuted
import com.example.ui.theme.PulseOnPrimary
import com.example.ui.theme.PulsePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBottomSheet(
    onDismiss: () -> Unit,
    onAddHabit: (title: String, subtitle: String, timeOfDay: HabitTimeOfDay, type: HabitType, target: Int, unit: String, colorHex: Long, iconName: String) -> Unit,
    onStartEmptyWorkout: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var habitTitle by remember { mutableStateOf("") }
    var habitTarget by remember { mutableStateOf("1") }
    var habitUnit by remember { mutableStateOf("times") }
    var selectedTimeOfDay by remember { mutableStateOf(HabitTimeOfDay.MORNING) }
    var selectedColorHex by remember { mutableStateOf(0xFF2447F5L) }
    var isCounterType by remember { mutableStateOf(false) }

    val colorOptions = listOf(
        0xFF2447F5L to HabitBlue,
        0xFF0A9F9DL to HabitTeal,
        0xFF0FA968L to HabitGreen,
        0xFFFF6A1FL to HabitOrange,
        0xFF7A3FF2L to HabitPurple,
        0xFFE5484DL to HabitRed,
        0xFF475569L to HabitSlate
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xF7FFFFFF),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 12.dp)
                .testTag("add_bottom_sheet")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "New Habit",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = PulseInk
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = PulseInkMuted)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Habit Name Input
            Text(text = "Habit Name", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PulseInkMuted)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = habitTitle,
                onValueChange = { habitTitle = it },
                placeholder = { Text("e.g. Read 15 mins, Drink Water...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("new_habit_title_input"),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PulsePrimary,
                    unfocusedBorderColor = Color(0xFFDDE3ED)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Time of Day Selector with fluid pill tabs
            Text(text = "Time of Day", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PulseInkMuted)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HabitTimeOfDay.values().forEach { time ->
                    val isSelected = selectedTimeOfDay == time
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .liquidGlassPill(
                                backgroundColor = if (isSelected) PulsePrimary else Color(0xFFF1F4FA),
                                borderColor = if (isSelected) PulsePrimary else Color.White,
                                elevation = if (isSelected) 4.dp else 1.dp
                            )
                            .bounceClick(scaleDown = 0.94f) { selectedTimeOfDay = time }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = time.title,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) PulseOnPrimary else PulseInk
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Color Swatches
            Text(text = "Color Theme", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PulseInkMuted)
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                colorOptions.forEach { (hex, color) ->
                    val isSelected = selectedColorHex == hex
                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 1.15f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "colorScale"
                    )

                    val borderMod = if (isSelected) Modifier.border(2.5.dp, Color.White, CircleShape) else Modifier

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .scale(scale)
                            .shadow(if (isSelected) 6.dp else 2.dp, CircleShape, spotColor = color)
                            .clip(CircleShape)
                            .background(color)
                            .then(borderMod)
                            .bounceClick(scaleDown = 0.90f, onClick = { selectedColorHex = hex }),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Create Habit button with iOS pill styling
            Button(
                onClick = {
                    if (habitTitle.isNotBlank()) {
                        onAddHabit(
                            habitTitle.trim(),
                            if (isCounterType) "0 / $habitTarget $habitUnit" else "Daily goal",
                            selectedTimeOfDay,
                            if (isCounterType) HabitType.COUNTER else HabitType.YES_NO,
                            habitTarget.toIntOrNull() ?: 1,
                            habitUnit,
                            selectedColorHex,
                            "check"
                        )
                    }
                },
                enabled = habitTitle.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(8.dp, CircleShape, spotColor = Color(0x402447F5))
                    .bounceClick(scaleDown = 0.95f)
                    .testTag("submit_create_habit_button"),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = PulsePrimary)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = PulseOnPrimary)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Add Habit", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PulseOnPrimary)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick alternative action
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlassCard(shape = RoundedCornerShape(18.dp), elevation = 2.dp)
                    .bounceClick(scaleDown = 0.96f) {
                        onDismiss()
                        onStartEmptyWorkout()
                    }
                    .padding(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.FitnessCenter, contentDescription = null, tint = PulsePrimary)
                    Text(
                        text = "Or Start an Empty Workout Session",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PulseInk
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

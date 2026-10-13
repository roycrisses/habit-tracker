package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.PulseInk
import com.example.ui.theme.PulseInkMuted
import com.example.ui.theme.PulseOnPrimary
import com.example.ui.theme.PulsePrimary
import com.example.ui.theme.PulseSuccess
import com.example.viewmodel.ActiveWorkoutSession

@Composable
fun ActiveWorkoutDialog(
    session: ActiveWorkoutSession,
    onPauseResume: () -> Unit,
    onFinish: () -> Unit,
    onCancel: () -> Unit
) {
    val minutes = session.elapsedSeconds / 60
    val seconds = session.elapsedSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    val infiniteTransition = rememberInfiniteTransition(label = "timerPulse")
    val timerPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "timerPulseAlpha"
    )

    Dialog(onDismissRequest = onCancel) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .liquidGlassCard(shape = RoundedCornerShape(28.dp), elevation = 16.dp)
                .padding(22.dp)
                .testTag("active_workout_dialog")
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with cancel
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .shadow(6.dp, RoundedCornerShape(12.dp), spotColor = Color(0x332447F5))
                                .clip(RoundedCornerShape(12.dp))
                                .background(PulsePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = PulseOnPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = "Workout in Progress",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = PulseInk
                        )
                    }

                    IconButton(onClick = onCancel) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel Workout",
                            tint = PulseInkMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Routine Title
                Text(
                    text = session.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = PulseInk
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Live Pulsing Timer Pill
                Box(
                    modifier = Modifier
                        .liquidGlassCard(
                            shape = RoundedCornerShape(22.dp),
                            elevation = 6.dp,
                            containerGradient = Brush.verticalGradient(
                                listOf(
                                    Color(0xFFEBF0FE),
                                    Color(0xFFDFE8FD)
                                )
                            ),
                            borderGradient = Brush.linearGradient(
                                listOf(
                                    Color.White,
                                    PulsePrimary.copy(alpha = timerPulseAlpha)
                                )
                            )
                        )
                        .padding(horizontal = 28.dp, vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = timeFormatted,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        color = PulsePrimary,
                        letterSpacing = (-1).sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .liquidGlassPill(
                            backgroundColor = if (session.isPaused) Color(0xFFFFF3DC) else PulseSuccess.copy(alpha = 0.15f),
                            borderColor = if (session.isPaused) Color(0xFFF5A400).copy(alpha = 0.4f) else PulseSuccess.copy(alpha = 0.3f),
                            elevation = 0.dp
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (session.isPaused) "⏸ PAUSED" else "● RECORDING LIVE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (session.isPaused) Color(0xFFC77E00) else PulseSuccess
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action buttons: Pause/Resume and Finish
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onPauseResume,
                        shape = CircleShape,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .bounceClick(scaleDown = 0.94f, onClick = onPauseResume)
                            .testTag("workout_pause_button")
                    ) {
                        Icon(
                            imageVector = if (session.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = null,
                            tint = PulseInk,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (session.isPaused) "Resume" else "Pause",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PulseInk
                        )
                    }

                    Button(
                        onClick = onFinish,
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = PulseSuccess),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .shadow(6.dp, CircleShape, spotColor = PulseSuccess)
                            .bounceClick(scaleDown = 0.94f, onClick = onFinish)
                            .testTag("workout_finish_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Finish",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

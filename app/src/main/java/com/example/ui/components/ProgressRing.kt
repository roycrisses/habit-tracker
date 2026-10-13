package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HabitTeal
import com.example.ui.theme.PulseInk
import com.example.ui.theme.PulseInkMuted
import com.example.ui.theme.PulsePrimary
import kotlin.math.cos
import kotlin.math.sin

/**
 * iOS-Style Liquid Glass Concentric Progress Ring.
 * Features:
 * - Fluid spring-animated dual rings (Habits & Steps)
 * - Luminous gradient strokes with specular glow tips
 * - Floating liquid glass frosted center core with specular rim
 * - Animated numbers counting up seamlessly
 * - Subtle ambient breathing glow
 */
@Composable
fun ConcentricProgressRing(
    habitsCompleted: Int,
    habitsTotal: Int,
    stepsCurrent: Int,
    stepsTarget: Int,
    size: Dp = 196.dp,
    modifier: Modifier = Modifier
) {
    val habitFraction = if (habitsTotal > 0) (habitsCompleted.toFloat() / habitsTotal).coerceIn(0f, 1f) else 0f
    val stepsFraction = if (stepsTarget > 0) (stepsCurrent.toFloat() / stepsTarget).coerceIn(0f, 1f) else 0f

    // Spring animations for rings
    val animatedHabitAngle by animateFloatAsState(
        targetValue = habitFraction * 360f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "iosHabitAngle"
    )

    val animatedStepsAngle by animateFloatAsState(
        targetValue = stepsFraction * 360f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "iosStepsAngle"
    )

    // Animated habit count for smooth fluid readout
    val animatedCount by animateIntAsState(
        targetValue = habitsCompleted,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "animatedCount"
    )

    // Ambient breathing glow for active tracker ring
    val infiniteTransition = rememberInfiniteTransition(label = "ringGlow")
    val pulseGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlowAlpha"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Ambient soft background glow behind rings
        Box(
            modifier = Modifier
                .size(size * 0.92f)
                .shadow(
                    elevation = 20.dp,
                    shape = CircleShape,
                    ambientColor = Color(0x1A2447F5),
                    spotColor = Color(0x332447F5).copy(alpha = pulseGlowAlpha)
                )
        )

        // Concentric Liquid Glass Rings
        Canvas(modifier = Modifier.size(size)) {
            val strokeWidthOuter = 13.dp.toPx()
            val strokeWidthInner = 10.dp.toPx()
            val gap = 6.5.dp.toPx()

            val outerRadius = (this.size.minDimension - strokeWidthOuter) / 2f
            val innerRadius = outerRadius - strokeWidthOuter - gap

            val centerOffset = Offset(this.size.width / 2f, this.size.height / 2f)

            // Outer Track (Habits) - Frosted glassy translucent groove
            drawCircle(
                color = Color(0xFFE9EDF5),
                radius = outerRadius,
                center = centerOffset,
                style = Stroke(width = strokeWidthOuter)
            )

            // Outer Arc - Fluid vibrant gradient
            if (animatedHabitAngle > 0f) {
                val habitGradient = Brush.sweepGradient(
                    colors = listOf(
                        Color(0xFF3864F8),
                        Color(0xFF1D3FE8),
                        Color(0xFF5B81FF),
                        Color(0xFF3864F8)
                    ),
                    center = centerOffset
                )

                drawArc(
                    brush = habitGradient,
                    startAngle = -90f,
                    sweepAngle = animatedHabitAngle,
                    useCenter = false,
                    topLeft = Offset(centerOffset.x - outerRadius, centerOffset.y - outerRadius),
                    size = Size(outerRadius * 2f, outerRadius * 2f),
                    style = Stroke(width = strokeWidthOuter, cap = StrokeCap.Round)
                )

                // Luminous head specular tip dot
                val habitHeadRad = Math.toRadians((animatedHabitAngle - 90.0)).toFloat()
                val tipX = centerOffset.x + outerRadius * cos(habitHeadRad)
                val tipY = centerOffset.y + outerRadius * sin(habitHeadRad)
                drawCircle(
                    color = Color.White.copy(alpha = 0.9f),
                    radius = strokeWidthOuter * 0.28f,
                    center = Offset(tipX, tipY)
                )
            }

            // Inner Track (Steps) - Frosted glassy track
            drawCircle(
                color = Color(0xFFE9EDF5),
                radius = innerRadius,
                center = centerOffset,
                style = Stroke(width = strokeWidthInner)
            )

            // Inner Arc (Steps) - Teal gradient
            if (animatedStepsAngle > 0f) {
                val stepsGradient = Brush.sweepGradient(
                    colors = listOf(
                        Color(0xFF00D492),
                        HabitTeal,
                        Color(0xFF2EE6AA),
                        Color(0xFF00D492)
                    ),
                    center = centerOffset
                )

                drawArc(
                    brush = stepsGradient,
                    startAngle = -90f,
                    sweepAngle = animatedStepsAngle,
                    useCenter = false,
                    topLeft = Offset(centerOffset.x - innerRadius, centerOffset.y - innerRadius),
                    size = Size(innerRadius * 2f, innerRadius * 2f),
                    style = Stroke(width = strokeWidthInner, cap = StrokeCap.Round)
                )

                // Tip specular highlight
                val stepsHeadRad = Math.toRadians((animatedStepsAngle - 90.0)).toFloat()
                val tipStepsX = centerOffset.x + innerRadius * cos(stepsHeadRad)
                val tipStepsY = centerOffset.y + innerRadius * sin(stepsHeadRad)
                drawCircle(
                    color = Color.White.copy(alpha = 0.9f),
                    radius = strokeWidthInner * 0.26f,
                    center = Offset(tipStepsX, tipStepsY)
                )
            }
        }

        // Floating Liquid Glass Center Core with specular rim
        Box(
            modifier = Modifier
                .size(size * 0.58f)
                .shadow(
                    elevation = 10.dp,
                    shape = CircleShape,
                    ambientColor = Color(0x140B0F1A),
                    spotColor = Color(0x1F2447F5)
                )
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFAFFFFFF),
                            Color(0xF0F5F8FC)
                        )
                    )
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            Color(0xFFFFFFFF),
                            Color(0x80FFFFFF),
                            Color(0x332447F5)
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$animatedCount/$habitsTotal",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = PulseInk,
                    letterSpacing = (-1).sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0xFFEBF0FE))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "habits",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PulsePrimary
                    )
                }
            }
        }
    }
}

package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HabitBlue
import com.example.ui.theme.PulseInk
import com.example.ui.theme.PulseInkMuted
import com.example.ui.theme.PulseLine
import com.example.ui.theme.PulsePrimary
import com.example.ui.theme.PulseSuccess

data class ConsistencyDay(
    val day: String,
    val percent: Int,
    val isToday: Boolean = false
)

@Composable
fun MatrixHeatmapBarChart(
    days: List<ConsistencyDay> = listOf(
        ConsistencyDay("M", 80),
        ConsistencyDay("T", 100),
        ConsistencyDay("W", 90),
        ConsistencyDay("T", 60),
        ConsistencyDay("F", 100),
        ConsistencyDay("S", 85),
        ConsistencyDay("S", 100, isToday = true)
    ),
    modifier: Modifier = Modifier
) {
    var isStarted by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        isStarted = true
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Selected day interactive liquid glass tooltip
        if (selectedIndex != null && selectedIndex!! in days.indices) {
            val selected = days[selectedIndex!!]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .liquidGlassPill(
                            backgroundColor = Color(0xF2FFFFFF),
                            borderColor = PulsePrimary.copy(alpha = 0.4f),
                            elevation = 4.dp
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Day ${selected.day}: ${selected.percent}% consistency ${if (selected.isToday) "(Today)" else ""}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PulsePrimary
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(134.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            days.forEachIndexed { index, item ->
                val targetFraction = (item.percent / 100f).coerceIn(0f, 1f)

                val animatedHeight by animateFloatAsState(
                    targetValue = if (isStarted) targetFraction else 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "barHeight_$index"
                )

                val isSelected = selectedIndex == index || (selectedIndex == null && item.isToday)
                val barGradient = if (item.percent < 70) {
                    Brush.verticalGradient(listOf(Color(0xFF6B8AFF), Color(0xFF3864F8)))
                } else {
                    Brush.verticalGradient(listOf(Color(0xFF3B67FF), Color(0xFF1D3FE8)))
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .bounceClick(scaleDown = 0.94f) {
                            selectedIndex = index
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    // Liquid Glass Capsule Pill Container
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFEDF1F7))
                            .then(
                                if (isSelected) {
                                    Modifier
                                        .shadow(6.dp, RoundedCornerShape(12.dp), spotColor = Color(0x332447F5))
                                        .border(2.dp, PulsePrimary, RoundedCornerShape(12.dp))
                                } else {
                                    Modifier.border(1.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                                }
                            )
                            .padding(3.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        // Liquid animated fill with rounded top cap
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(animatedHeight)
                                .clip(RoundedCornerShape(10.dp))
                                .background(barGradient)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = item.day,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) PulsePrimary else PulseInk
                    )
                    Text(
                        text = "${item.percent}%",
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) PulsePrimary else PulseInkMuted
                    )
                }
            }
        }

        // Intensity legend
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Consistency",
                fontSize = 10.sp,
                color = PulseInkMuted,
                modifier = Modifier.padding(end = 6.dp)
            )
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFDDE3ED)))
            Spacer(modifier = Modifier.width(4.dp))
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(PulsePrimary.copy(alpha = 0.4f)))
            Spacer(modifier = Modifier.width(4.dp))
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(PulsePrimary.copy(alpha = 0.7f)))
            Spacer(modifier = Modifier.width(4.dp))
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(PulsePrimary))
        }
    }
}

data class StepsDay(
    val day: String,
    val steps: Int,
    val isGoalMet: Boolean
)

@Composable
fun StepsTrendBarChart(
    days: List<StepsDay> = listOf(
        StepsDay("M", 7420, false),
        StepsDay("T", 10850, true),
        StepsDay("W", 8100, false),
        StepsDay("T", 6400, false),
        StepsDay("F", 11200, true),
        StepsDay("S", 12470, true),
        StepsDay("S", 6000, false)
    ),
    goalSteps: Int = 10000,
    modifier: Modifier = Modifier
) {
    val maxSteps = 14000f

    var isStarted by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isStarted = true
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(134.dp)
        ) {
            // Target guide line canvas with glowing dot
            Canvas(modifier = Modifier.fillMaxSize()) {
                val goalY = size.height * (1f - (goalSteps / maxSteps))
                drawLine(
                    color = Color(0xFFC8D3E5),
                    start = Offset(0f, goalY),
                    end = Offset(size.width, goalY),
                    strokeWidth = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )

                // Goal indicator tag dot on left
                drawCircle(
                    color = PulseSuccess,
                    radius = 3.5.dp.toPx(),
                    center = Offset(6.dp.toPx(), goalY)
                )
            }

            // Steps bars
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                days.forEachIndexed { index, item ->
                    val fraction = (item.steps / maxSteps).coerceIn(0.1f, 1f)
                    val animatedFraction by animateFloatAsState(
                        targetValue = if (isStarted) fraction else 0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "stepBar_$index"
                    )

                    val barGradient = if (item.isGoalMet) {
                        Brush.verticalGradient(listOf(Color(0xFF00E59B), PulseSuccess))
                    } else {
                        Brush.verticalGradient(listOf(Color(0xFFE2E7F0), Color(0xFFCBD5E1)))
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(animatedFraction)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(barGradient)
                                    .then(
                                        if (item.isGoalMet) {
                                            Modifier.shadow(4.dp, RoundedCornerShape(10.dp), spotColor = Color(0x330FA968))
                                        } else Modifier
                                    )
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = item.day,
                            fontSize = 12.sp,
                            fontWeight = if (item.isGoalMet) FontWeight.Bold else FontWeight.Medium,
                            color = if (item.isGoalMet) PulseSuccess else PulseInk
                        )
                        Text(
                            text = "${item.steps / 1000}k",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = PulseInkMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BodyWeightLineChart(
    weights: List<Float> = listOf(73.2f, 73.0f, 73.1f, 72.8f, 72.9f, 72.6f, 72.4f),
    labels: List<String> = listOf("May 1", "May 5", "May 9", "May 13", "May 17", "May 21", "Today"),
    modifier: Modifier = Modifier
) {
    if (weights.isEmpty()) return

    val minWeight = weights.minOrNull() ?: 70f
    val maxWeight = weights.maxOrNull() ?: 75f
    val range = (maxWeight - minWeight).coerceAtLeast(1.0f)

    var isStarted by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isStarted = true
    }

    val animatedProgress by animateFloatAsState(
        targetValue = if (isStarted) 1f else 0f,
        animationSpec = tween(900),
        label = "weightChartProgress"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val paddingX = 16.dp.toPx()
                val paddingY = 16.dp.toPx()
                val stepX = (w - paddingX * 2) / (weights.size - 1).coerceAtLeast(1)

                val points = weights.mapIndexed { idx, wt ->
                    val x = paddingX + idx * stepX
                    val normalized = (wt - minWeight) / range
                    val y = (h - paddingY) - normalized * (h - paddingY * 2)
                    Offset(x, y)
                }

                // Grid lines
                for (i in 0..2) {
                    val y = paddingY + i * ((h - paddingY * 2) / 2)
                    drawLine(
                        color = Color(0xFFEDF1F7),
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                if (points.size >= 2) {
                    // Smooth curved path
                    val path = Path()
                    val fillPath = Path()

                    path.moveTo(points.first().x, points.first().y)
                    fillPath.moveTo(points.first().x, h)
                    fillPath.lineTo(points.first().x, points.first().y)

                    for (i in 0 until points.size - 1) {
                        val p0 = points[i]
                        val p1 = points[i + 1]
                        val cx = (p0.x + p1.x) / 2
                        path.cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                        fillPath.cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                    }

                    fillPath.lineTo(points.last().x, h)
                    fillPath.close()

                    // Gradient fill under the curve (area chart)
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            listOf(
                                PulsePrimary.copy(alpha = 0.22f * animatedProgress),
                                Color.Transparent
                            )
                        )
                    )

                    // Line curve with primary color
                    drawPath(
                        path = path,
                        color = PulsePrimary,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Data points with luminous halos
                    points.forEachIndexed { index, pt ->
                        val isLast = index == points.size - 1
                        val pointRadius = if (isLast) 5.5.dp.toPx() else 4.dp.toPx()

                        // Outer halo
                        drawCircle(
                            color = if (isLast) PulsePrimary else PulsePrimary.copy(alpha = 0.3f),
                            radius = pointRadius + 2.dp.toPx(),
                            center = pt
                        )
                        // White inner core
                        drawCircle(
                            color = Color.White,
                            radius = pointRadius,
                            center = pt
                        )
                        // Center dot
                        drawCircle(
                            color = PulsePrimary,
                            radius = pointRadius * 0.5f,
                            center = pt
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = labels.firstOrNull() ?: "", fontSize = 11.sp, color = PulseInkMuted)
            Text(text = labels.getOrNull(labels.size / 2) ?: "", fontSize = 11.sp, color = PulseInkMuted)
            Text(text = labels.lastOrNull() ?: "", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PulsePrimary)
        }
    }
}

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
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.MilestoneBadge
import com.example.ui.components.BodyWeightLineChart
import com.example.ui.components.MatrixHeatmapBarChart
import com.example.ui.components.StepsTrendBarChart
import com.example.ui.components.bounceClick
import com.example.ui.components.liquidGlassCard
import com.example.ui.components.liquidGlassPill
import com.example.ui.theme.HabitBlue
import com.example.ui.theme.PulseInk
import com.example.ui.theme.PulseInkMuted
import com.example.ui.theme.PulseOnPrimary
import com.example.ui.theme.PulsePrimary
import com.example.ui.theme.PulseStreak
import com.example.ui.theme.PulseSuccess
import com.example.viewmodel.ProgressRange
import com.example.viewmodel.PulseHabitUiState

const val INSIGHT_IMAGE_URL =
    "https://lh3.googleusercontent.com/aida-public/AB6AXuCfsFKaGWIDBbVvWvyyb3WihaNIEJmiuWVf1UCsqY70S1G9k3n-OU83Oughh14FvdJq7kTyMwkGKNaAAC1I4jTAexp4pnA1MRYhPMYh5Kv3l-t-SVQ7CZzm2hvyJTze5gIAMfnTaccth-CvRHEOPHkGwv6uJnxdBUu7ok8rG6J22mATkpRN3FvfkQNdN2-XY5IF9WuVwGCFA0kGHlqZNQDoVv4oovsD4v8FzElW_MiwQpFeo_kMEKBP"

const val REPORT_EXPORT_IMAGE_URL =
    "https://lh3.googleusercontent.com/aida-public/AB6AXuCuN-4I1KDx7DBs-dYq-GHpvBrhm2URYJYala6lpJ7YwuNGy6u8sOWtwkbeSo6g2hMfeUlfT-TF0spaiFyip2BTtZ6aTO7PlJN4OG6UBr2Mgz2iN6QRRE8HzHONoawxYZmaQd7Y0Rz9wtNkGkY8aG1Z_N1Ki_UWoo8Unkk9-TA2Zv7O6yaiDW25dHLu6hyRogPBR4FLkpzFfmkAwraIbMmZ6uzZobHyrKWyhV2S7NzfCHjAMbaDH7Wg"

@Composable
fun ProgressScreen(
    state: PulseHabitUiState,
    onRangeSelected: (ProgressRange) -> Unit,
    onExportReport: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // iOS Segmented Liquid Glass Range Switch (Week, Month, Year)
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
                    ProgressRange.values().forEach { range ->
                        val isSelected = state.progressRange == range

                        val tabScale by animateFloatAsState(
                            targetValue = if (isSelected) 1.02f else 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            label = "rangeScale_${range.name}"
                        )

                        val tabBgColor by animateColorAsState(
                            targetValue = if (isSelected) PulseInk else Color.Transparent,
                            animationSpec = tween(200),
                            label = "rangeBg_${range.name}"
                        )

                        val tabTextColor by animateColorAsState(
                            targetValue = if (isSelected) PulseOnPrimary else PulseInkMuted,
                            animationSpec = tween(200),
                            label = "rangeText_${range.name}"
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
                                .bounceClick(scaleDown = 0.94f) { onRangeSelected(range) }
                                .padding(vertical = 10.dp)
                                .testTag("progress_range_${range.name.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (range) {
                                    ProgressRange.WEEK -> "Week"
                                    ProgressRange.MONTH -> "Month"
                                    ProgressRange.YEAR -> "Year"
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

        // High Momentum Banner - iOS Liquid Glass Surface
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlassCard(
                        shape = RoundedCornerShape(24.dp),
                        elevation = 6.dp,
                        containerGradient = Brush.verticalGradient(
                            listOf(
                                Color(0xFFEFF4FF),
                                Color(0xFFE4EDFD)
                            )
                        ),
                        borderGradient = Brush.linearGradient(
                            listOf(
                                Color.White,
                                Color(0x662447F5)
                            )
                        )
                    )
                    .testTag("high_momentum_banner")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = PulsePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "HIGH MOMENTUM",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PulsePrimary,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Top 5% consistency this week",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PulseInk
                        )
                        Text(
                            text = "You completed 92% of scheduled habits on workout days.",
                            fontSize = 12.sp,
                            color = PulseInkMuted
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .shadow(6.dp, CircleShape, spotColor = Color(0x332447F5))
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.5.dp, PulsePrimary.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🔥", fontSize = 24.sp)
                    }
                }
            }
        }

        // Streak & Consistency Cards (2 Columns)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Current Streak Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .liquidGlassCard(shape = RoundedCornerShape(22.dp), elevation = 4.dp)
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = PulseStreak,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Current Streak",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PulseInkMuted
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${state.streakDays}d",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = PulseInk
                        )
                        Text(
                            text = "Best: 21d (Personal Record)",
                            fontSize = 11.sp,
                            color = PulseInkMuted
                        )
                    }
                }

                // Weekly Completion Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .liquidGlassCard(shape = RoundedCornerShape(22.dp), elevation = 4.dp)
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = PulsePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Completion",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PulseInkMuted
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${state.weeklyCompletionPercent}%",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = PulseInk
                        )
                        Text(
                            text = "+12% vs last week",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PulseSuccess
                        )
                    }
                }
            }
        }

        // Habit Consistency Heatmap Bar Chart Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlassCard(shape = RoundedCornerShape(26.dp), elevation = 6.dp)
                    .padding(18.dp)
                    .testTag("habit_consistency_chart_card")
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Habit Consistency",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = PulseInk
                            )
                            Text(
                                text = "Daily completion % over the week",
                                fontSize = 12.sp,
                                color = PulseInkMuted
                            )
                        }
                        Box(
                            modifier = Modifier
                                .liquidGlassPill(
                                    backgroundColor = Color(0xFFEBF0FE),
                                    borderColor = PulsePrimary.copy(alpha = 0.25f),
                                    elevation = 0.dp
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "Avg 88%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PulsePrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    MatrixHeatmapBarChart(days = state.consistencyDays)
                }
            }
        }

        // Daily Steps Trend Chart Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlassCard(shape = RoundedCornerShape(26.dp), elevation = 6.dp)
                    .padding(18.dp)
                    .testTag("steps_trend_chart_card")
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Step Activity",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = PulseInk
                            )
                            Text(
                                text = "Daily step count & 10k target",
                                fontSize = 12.sp,
                                color = PulseInkMuted
                            )
                        }
                        Box(
                            modifier = Modifier
                                .liquidGlassPill(
                                    backgroundColor = PulseSuccess.copy(alpha = 0.15f),
                                    borderColor = PulseSuccess.copy(alpha = 0.3f),
                                    elevation = 0.dp
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "4 of 7 Met",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PulseSuccess
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    StepsTrendBarChart(days = state.stepsDays)
                }
            }
        }

        // Body Weight Trend Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlassCard(shape = RoundedCornerShape(26.dp), elevation = 6.dp)
                    .padding(18.dp)
                    .testTag("body_weight_chart_card")
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Weight Trend",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = PulseInk
                            )
                            Text(
                                text = "Target: ${state.targetWeightKg} kg",
                                fontSize = 12.sp,
                                color = PulseInkMuted
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${state.currentWeightKg} kg",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = PulseInk
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.TrendingDown,
                                    contentDescription = null,
                                    tint = PulseSuccess,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = " -0.8 kg this month",
                                    fontSize = 11.sp,
                                    color = PulseSuccess,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    BodyWeightLineChart(weights = state.weights)
                }
            }
        }

        // Badges & Milestones Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Badges & Milestones",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PulseInk
                    )
                    Text(
                        text = "3 of 8 unlocked",
                        fontSize = 12.sp,
                        color = PulseInkMuted
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    state.milestoneBadges.take(2).forEach { badge ->
                        MilestoneBadgeTile(badge = badge, modifier = Modifier.weight(1f))
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    state.milestoneBadges.drop(2).take(2).forEach { badge ->
                        MilestoneBadgeTile(badge = badge, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Export Monthly Report Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlassCard(shape = RoundedCornerShape(24.dp), elevation = 6.dp)
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Export Monthly Report",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PulseInk
                        )
                        Text(
                            text = "Generate comprehensive CSV or JSON summaries of your habits and workouts.",
                            fontSize = 12.sp,
                            color = PulseInkMuted
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onExportReport,
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = PulsePrimary),
                            modifier = Modifier
                                .height(42.dp)
                                .shadow(6.dp, CircleShape, spotColor = Color(0x332447F5))
                                .bounceClick(scaleDown = 0.94f, onClick = onExportReport)
                        ) {
                            Icon(
                                imageVector = Icons.Default.IosShare,
                                contentDescription = null,
                                tint = PulseOnPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Share Report",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PulseOnPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF1F5F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = REPORT_EXPORT_IMAGE_URL,
                            contentDescription = "Report Illustration",
                            modifier = Modifier.size(56.dp),
                            contentScale = ContentScale.Fit
                        )
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
fun MilestoneBadgeTile(
    badge: MilestoneBadge,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .liquidGlassCard(
                shape = RoundedCornerShape(20.dp),
                elevation = if (badge.isUnlocked) 5.dp else 2.dp,
                containerGradient = Brush.verticalGradient(
                    if (badge.isUnlocked) listOf(Color(0xFAFFFFFF), Color(0xF2F6F9FD))
                    else listOf(Color(0xEDF1F5F9), Color(0xE0E2E8F0))
                )
            )
            .padding(14.dp)
            .bounceClick(scaleDown = 0.95f)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .shadow(
                        elevation = if (badge.isUnlocked) 8.dp else 0.dp,
                        shape = CircleShape,
                        spotColor = Color(badge.colorHex)
                    )
                    .clip(CircleShape)
                    .background(
                        if (badge.isUnlocked) Color(badge.colorHex)
                        else Color(0xFFCBD5E1)
                    )
                    .border(1.5.dp, Color.White.copy(alpha = 0.7f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (badge.isUnlocked) {
                    if (badge.badgeNumber != null) {
                        Text(
                            text = badge.badgeNumber,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    } else {
                        Icon(
                            imageVector = when (badge.iconName) {
                                "directions_walk" -> Icons.Default.DirectionsWalk
                                "workspace_premium" -> Icons.Default.WorkspacePremium
                                else -> Icons.Default.LocalFireDepartment
                            },
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = badge.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = PulseInk
            )
            Text(
                text = badge.subtitle,
                fontSize = 11.sp,
                color = PulseInkMuted
            )
        }
    }
}

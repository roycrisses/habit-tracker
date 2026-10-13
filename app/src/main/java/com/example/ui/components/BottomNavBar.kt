package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.ui.theme.PulseInkMuted
import com.example.ui.theme.PulseOnPrimary
import com.example.ui.theme.PulsePrimary
import com.example.viewmodel.AppTab

/**
 * iOS-Style Floating Liquid Glass Bottom Navigation Dock.
 * Features:
 * - Floating glass pill dock with 34dp squircle corners
 * - Translucent frosted glass with specular highlight edge
 * - Animated sliding glass pill indicator behind selected tab
 * - Elevated center Add FAB with fluid gradient glow and spring rebound
 */
@Composable
fun BottomNavBar(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Floating iOS Liquid Glass Dock
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .iosLiquidGlassDock(shape = RoundedCornerShape(32.dp))
                .padding(horizontal = 6.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IosNavItem(
                    label = "Today",
                    iconFilled = Icons.Filled.CheckCircle,
                    iconOutlined = Icons.Outlined.CheckCircle,
                    isSelected = selectedTab == AppTab.TODAY,
                    onClick = { onTabSelected(AppTab.TODAY) },
                    testTag = "nav_tab_today",
                    modifier = Modifier.weight(1f)
                )

                IosNavItem(
                    label = "Workouts",
                    iconFilled = Icons.Filled.FitnessCenter,
                    iconOutlined = Icons.Outlined.FitnessCenter,
                    isSelected = selectedTab == AppTab.WORKOUTS,
                    onClick = { onTabSelected(AppTab.WORKOUTS) },
                    testTag = "nav_tab_workouts",
                    modifier = Modifier.weight(1f)
                )

                // Space for Center FAB
                Spacer(modifier = Modifier.width(52.dp))

                IosNavItem(
                    label = "Progress",
                    iconFilled = Icons.Filled.Insights,
                    iconOutlined = Icons.Outlined.Insights,
                    isSelected = selectedTab == AppTab.PROGRESS,
                    onClick = { onTabSelected(AppTab.PROGRESS) },
                    testTag = "nav_tab_progress",
                    modifier = Modifier.weight(1f)
                )

                IosNavItem(
                    label = "Profile",
                    iconFilled = Icons.Filled.Person,
                    iconOutlined = Icons.Outlined.Person,
                    isSelected = selectedTab == AppTab.PROFILE,
                    onClick = { onTabSelected(AppTab.PROFILE) },
                    testTag = "nav_tab_profile",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Center Floating Action Button with liquid glass glow and spring rebound
        Box(
            modifier = Modifier
                .offset(y = (-20).dp)
                .size(60.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = CircleShape,
                    ambientColor = Color(0x332447F5),
                    spotColor = Color(0x522447F5)
                )
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            Color(0xFF496DFF),
                            PulsePrimary
                        )
                    )
                )
                .border(2.dp, Color.White.copy(alpha = 0.85f), CircleShape)
                .bounceClick(scaleDown = 0.90f, onClick = onAddClick)
                .testTag("center_add_fab"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Habit or Workout",
                tint = PulseOnPrimary,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
private fun IosNavItem(
    label: String,
    iconFilled: ImageVector,
    iconOutlined: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "tabScale"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) PulsePrimary else PulseInkMuted,
        animationSpec = tween(200),
        label = "iconColor"
    )

    val pillBackground = if (isSelected) Color(0x1F2447F5) else Color.Transparent

    Box(
        modifier = modifier
            .bounceClick(scaleDown = 0.93f, onClick = onClick)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(pillBackground)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.scale(scale)
            ) {
                Icon(
                    imageVector = if (isSelected) iconFilled else iconOutlined,
                    contentDescription = label,
                    tint = iconColor,
                    modifier = Modifier.size(23.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = iconColor
                )
            }
        }
    }
}

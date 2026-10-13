package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.components.PROFILE_AVATAR_URL
import com.example.ui.components.bounceClick
import com.example.ui.components.liquidGlassCard
import com.example.ui.components.liquidGlassPill
import com.example.ui.theme.HabitBlue
import com.example.ui.theme.HabitOrange
import com.example.ui.theme.HabitPurple
import com.example.ui.theme.HabitTeal
import com.example.ui.theme.PulseError
import com.example.ui.theme.PulseInk
import com.example.ui.theme.PulseInkMuted
import com.example.ui.theme.PulseOnPrimary
import com.example.ui.theme.PulsePrimary
import com.example.ui.theme.PulseStreak
import com.example.ui.theme.PulseSuccess
import com.example.viewmodel.PulseHabitUiState

@Composable
fun ProfileScreen(
    state: PulseHabitUiState,
    onSyncHealthConnect: () -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onToggleAppLock: (Boolean) -> Unit,
    onExportData: () -> Unit,
    onDeleteAccount: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Profile Hero Card - iOS Liquid Glass Surface
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .liquidGlassCard(shape = RoundedCornerShape(26.dp), elevation = 8.dp)
                    .padding(20.dp)
                    .testTag("profile_hero_card")
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .shadow(10.dp, CircleShape, spotColor = Color(0x33000000))
                            .clip(CircleShape)
                            .background(Color(0xFFE2E8F0))
                            .border(2.5.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = PROFILE_AVATAR_URL,
                            contentDescription = "Alex Morgan",
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Alex Morgan",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = PulseInk
                        )
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified Member",
                            tint = PulsePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Text(
                        text = "Consistency Champion · Level 6",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = PulsePrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .liquidGlassPill(
                                backgroundColor = Color(0xF2FFFFFF),
                                borderColor = Color(0xFFE2E8F0),
                                elevation = 1.dp
                            )
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Member since Jan 2024",
                            fontSize = 11.sp,
                            color = PulseInkMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3-Column Quick Stats Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ProfileStatTile(
                            icon = Icons.Default.LocalFireDepartment,
                            iconTint = PulseStreak,
                            value = "${state.streakDays}d",
                            label = "Streak",
                            modifier = Modifier.weight(1f)
                        )
                        ProfileStatTile(
                            icon = Icons.Default.TaskAlt,
                            iconTint = PulseSuccess,
                            value = "${state.habitsMetTotal}",
                            label = "Habits Met",
                            modifier = Modifier.weight(1f)
                        )
                        ProfileStatTile(
                            icon = Icons.Default.FitnessCenter,
                            iconTint = PulsePrimary,
                            value = "${state.workoutsTotal}",
                            label = "Workouts",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Health Connect Integration Card
        item {
            val infiniteTransition = rememberInfiniteTransition(label = "syncRotation")
            val rotationAngle by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1400, easing = LinearEasing)
                ),
                label = "syncSpin"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlassCard(shape = RoundedCornerShape(24.dp), elevation = 6.dp)
                    .padding(18.dp)
                    .testTag("health_connect_sync_card")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .shadow(4.dp, RoundedCornerShape(14.dp), spotColor = Color(0x260FA968))
                                .clip(RoundedCornerShape(14.dp))
                                .background(PulseSuccess.copy(alpha = 0.15f))
                                .border(1.dp, PulseSuccess.copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsWalk,
                                contentDescription = null,
                                tint = PulseSuccess,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Health Connect",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = PulseInk
                            )
                            Text(
                                text = state.healthSyncText,
                                fontSize = 12.sp,
                                color = PulseInkMuted
                            )
                        }
                    }

                    Button(
                        onClick = onSyncHealthConnect,
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = PulsePrimary),
                        modifier = Modifier
                            .height(38.dp)
                            .shadow(4.dp, CircleShape, spotColor = Color(0x332447F5))
                            .bounceClick(scaleDown = 0.94f, onClick = onSyncHealthConnect)
                            .testTag("sync_health_connect_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Sync",
                            tint = PulseOnPrimary,
                            modifier = Modifier
                                .size(16.dp)
                                .rotate(if (state.isHealthSyncing) rotationAngle else 0f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (state.isHealthSyncing) "Syncing" else "Sync",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PulseOnPrimary
                        )
                    }
                }
            }
        }

        // Section: Goals
        item {
            SettingsGroup(title = "Habits & Fitness Goals") {
                SettingsRow(
                    icon = Icons.Default.DirectionsWalk,
                    title = "Daily Step Goal",
                    value = "10,000 steps"
                )
                HorizontalDivider(color = Color(0xFFEDF1F7), thickness = 1.dp)
                SettingsRow(
                    icon = Icons.Default.FitnessCenter,
                    title = "Weekly Workouts Goal",
                    value = "4 sessions"
                )
                HorizontalDivider(color = Color(0xFFEDF1F7), thickness = 1.dp)
                SettingsRow(
                    icon = Icons.Default.Schedule,
                    title = "Habit Reminders",
                    value = "08:00 AM, 08:30 PM"
                )
            }
        }

        // Section: Preferences
        item {
            SettingsGroup(title = "Preferences") {
                SettingsRow(
                    icon = Icons.Default.Straighten,
                    title = "Units",
                    value = state.units
                )
                HorizontalDivider(color = Color(0xFFEDF1F7), thickness = 1.dp)
                SettingsRow(
                    icon = Icons.Default.CalendarToday,
                    title = "First Day of Week",
                    value = state.weekStart
                )
                HorizontalDivider(color = Color(0xFFEDF1F7), thickness = 1.dp)
                SettingsToggleRow(
                    icon = Icons.Default.NotificationsActive,
                    title = "Push Notifications",
                    checked = state.notificationsEnabled,
                    onCheckedChange = onToggleNotifications
                )
                HorizontalDivider(color = Color(0xFFEDF1F7), thickness = 1.dp)
                SettingsToggleRow(
                    icon = Icons.Default.Lock,
                    title = "App Lock (Biometrics/PIN)",
                    checked = state.appLockEnabled,
                    onCheckedChange = onToggleAppLock
                )
            }
        }

        // Section: Data & Privacy
        item {
            SettingsGroup(title = "Data & Privacy") {
                SettingsRow(
                    icon = Icons.Default.CloudDone,
                    title = "Cloud Sync & Backup",
                    value = "Up to date"
                )
                HorizontalDivider(color = Color(0xFFEDF1F7), thickness = 1.dp)
                SettingsClickableRow(
                    icon = Icons.Default.FileDownload,
                    title = "Export Data (CSV / JSON)",
                    onClick = onExportData,
                    testTag = "export_data_row"
                )
                HorizontalDivider(color = Color(0xFFEDF1F7), thickness = 1.dp)
                SettingsClickableRow(
                    icon = Icons.Default.DeleteForever,
                    title = "Delete Account & Data",
                    titleColor = PulseError,
                    onClick = { showDeleteConfirmDialog = true },
                    testTag = "delete_account_row"
                )
            }
        }

        // Version Footer
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "PulseHabit v1.0.0 (Build 42)",
                    fontSize = 12.sp,
                    color = PulseInkMuted
                )
                Text(
                    text = "Crafted for physical and mental consistency",
                    fontSize = 11.sp,
                    color = PulseInkMuted.copy(alpha = 0.7f)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(90.dp))
        }
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Delete All Data?", fontWeight = FontWeight.Bold) },
            text = { Text("This will permanently remove all your habits, completed logs, routines, and metrics from the local database.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDeleteAccount()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PulseError)
                ) {
                    Text("Delete Everything", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel", color = PulseInk)
                }
            }
        )
    }
}

@Composable
fun ProfileStatTile(
    icon: ImageVector,
    iconTint: Color,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .liquidGlassCard(shape = RoundedCornerShape(18.dp), elevation = 3.dp)
            .padding(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                color = PulseInk
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = PulseInkMuted
            )
        }
    }
}

@Composable
fun SettingsGroup(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = PulseInkMuted,
            letterSpacing = 0.3.sp
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .liquidGlassCard(shape = RoundedCornerShape(22.dp), elevation = 4.dp)
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = PulseInkMuted, modifier = Modifier.size(20.dp))
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = PulseInk)
        }
        Text(text = value, fontSize = 13.sp, color = PulseInkMuted)
    }
}

@Composable
fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = PulseInkMuted, modifier = Modifier.size(20.dp))
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = PulseInk)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PulsePrimary,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFCBD5E1)
            )
        )
    }
}

@Composable
fun SettingsClickableRow(
    icon: ImageVector,
    title: String,
    titleColor: Color = PulseInk,
    onClick: () -> Unit,
    testTag: String = ""
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick(scaleDown = 0.98f, onClick = onClick)
            .testTag(testTag)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = if (titleColor == PulseError) PulseError else PulseInkMuted, modifier = Modifier.size(20.dp))
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = titleColor)
        }
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = PulseInkMuted, modifier = Modifier.size(18.dp))
    }
}

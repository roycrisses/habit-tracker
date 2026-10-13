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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.PulseInk
import com.example.ui.theme.PulsePrimary
import com.example.ui.theme.PulseSuccess
import com.example.ui.theme.PulseSurfaceStrong

const val PROFILE_AVATAR_URL =
    "https://lh3.googleusercontent.com/aida-public/AB6AXuDaiLav0a2hrdLa9-BCrobrcYcQ-kBOlRhx0iTciOtEQSeY9o1Gb00nUhWzEE0IZwLAzZLY4lX4UU4JRVVIhepl9sKJJalqgLlPlFcmubKMRBwrsoNY-XXATwQUJx-aTkN327rBhNmzo5DNusYeJVdDf3LLmNoi5Cyc6rqa3OWNGsEmfs3vgXSZEDj0rfQO5zTd9GizGbN9UOthkm0C13SXaZ_mNfNv-9lFflkMROCo8vqVW3JdVNcs"

const val PULSE_LOGO_URL =
    "https://lh3.googleusercontent.com/aida/AEtjO1X7KJwjceWkAh734siz_cRyuElZyqx7auOUXnTyfZN7449p4nutvF_-zYt093gghMKWvBanFxferjBOfX0tafbvLBnl7-5Dw6U47hq37waS5qNxzPgdmucjR84E1DXwYovC4-M78gm0pHRDxEEq6uw1-2wk1IErVcsvoCirPGBYU-jomUu2l9q1UaWbX60Ak-mAbnpVcIj4uNl6bVPE1p6_xly91jc2tPmrTaGmQBKGQCiaqXeIUg3MxT4"

/**
 * iOS-Style Translucent Frosted Glass Top Header Bar.
 * Features:
 * - Frosted blur background with specular lower highlight
 * - Glowing pulse icon with spring tactile response
 * - Avatar with live status indicator dot and specular rim ring
 */
@Composable
fun TopHeaderBar(
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "avatarPulse")
    val livePulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "livePulse"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xF5FFFFFF),
                        Color(0xE6F8FAFD)
                    )
                )
            )
            .border(
                androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Brush.verticalGradient(
                        listOf(
                            Color.White,
                            Color(0x33CBD5E1)
                        )
                    )
                )
            )
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo & Brand Name with tactile bounce
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .bounceClick(scaleDown = 0.95f)
                .testTag("app_brand_header")
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = CircleShape,
                        spotColor = Color(0x3D2447F5),
                        ambientColor = Color(0x1F2447F5)
                    )
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFF4C6FFF), PulsePrimary)
                        )
                    )
                    .border(1.5.dp, Color.White.copy(alpha = 0.85f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = PULSE_LOGO_URL,
                    contentDescription = "PulseHabit Logo",
                    modifier = Modifier.size(24.dp),
                    contentScale = ContentScale.Fit
                )
            }
            Text(
                text = "PulseHabit",
                fontSize = 21.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PulseInk,
                letterSpacing = (-0.5).sp
            )
        }

        // Profile Avatar Button with live status indicator
        Box(
            modifier = Modifier
                .bounceClick(scaleDown = 0.92f, onClick = onProfileClick)
                .testTag("profile_avatar_button"),
            contentAlignment = Alignment.BottomEnd
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .shadow(
                        elevation = 6.dp,
                        shape = CircleShape,
                        spotColor = Color(0x260B0F1A)
                    )
                    .clip(CircleShape)
                    .background(PulseSurfaceStrong)
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = PROFILE_AVATAR_URL,
                    contentDescription = "Alex Morgan Profile",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            // Live status dot
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(PulseSuccess.copy(alpha = livePulseAlpha))
                    .border(2.dp, Color.White, CircleShape)
            )
        }
    }
}

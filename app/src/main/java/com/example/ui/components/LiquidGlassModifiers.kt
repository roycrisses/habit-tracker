package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// iOS Glass Tokens
val IosGlassSurfaceLight = Color(0xF7FFFFFF)
val IosGlassSurfaceSubtle = Color(0xEDF8FAFD)
val IosGlassBorderHighlight = Color(0xFFFFFFFF)
val IosGlassBorderShadow = Color(0x280B0F1A)
val IosGlassBlueSheen = Color(0x1F2447F5)

/**
 * iOS-Style Liquid Glass Card Modifier.
 * Features:
 * - Ultra-rounded squircles (defaults to 24dp)
 * - Multi-stop translucent glass gradient
 * - Specular rim highlight border
 * - Deep, soft iOS ambient shadow
 */
fun Modifier.liquidGlassCard(
    shape: Shape = RoundedCornerShape(24.dp),
    elevation: Dp = 8.dp,
    containerGradient: Brush = Brush.verticalGradient(
        listOf(
            Color(0xFAFFFFFF),
            Color(0xF0F7F9FC)
        )
    ),
    borderGradient: Brush = Brush.linearGradient(
        listOf(
            Color(0xFFFFFFFF),
            Color(0x80FFFFFF),
            Color(0x242447F5)
        )
    )
): Modifier = this
    .shadow(
        elevation = elevation,
        shape = shape,
        ambientColor = Color(0x10000000),
        spotColor = Color(0x1A2447F5)
    )
    .clip(shape)
    .background(containerGradient)
    .border(BorderStroke(1.2.dp, borderGradient), shape)

/**
 * iOS-Style Rounded Full Pill Modifier for chips, tags, and small badges.
 */
fun Modifier.liquidGlassPill(
    shape: Shape = RoundedCornerShape(percent = 50),
    backgroundColor: Color = Color(0xE8F4F6FB),
    borderColor: Color = Color(0x80FFFFFF),
    elevation: Dp = 2.dp
): Modifier = this
    .shadow(elevation = elevation, shape = shape, spotColor = Color(0x14000000))
    .clip(shape)
    .background(backgroundColor)
    .border(BorderStroke(1.dp, borderColor), shape)

/**
 * iOS Floating Liquid Glass Dock for navigation bar.
 */
fun Modifier.iosLiquidGlassDock(
    shape: Shape = RoundedCornerShape(32.dp)
): Modifier = this
    .shadow(
        elevation = 16.dp,
        shape = shape,
        ambientColor = Color(0x140B0F1A),
        spotColor = Color(0x2E2447F5)
    )
    .clip(shape)
    .background(
        Brush.verticalGradient(
            listOf(
                Color(0xF5FFFFFF),
                Color(0xECF6F8FC)
            )
        )
    )
    .border(
        BorderStroke(
            1.5.dp,
            Brush.linearGradient(
                listOf(
                    Color(0xFFFFFFFF),
                    Color(0xB3FFFFFF),
                    Color(0x3D2447F5)
                )
            )
        ),
        shape
    )

/**
 * Fluid spring tactile bounce on touch for buttons and cards.
 */
fun Modifier.bounceClick(
    scaleDown: Float = 0.94f,
    onClick: (() -> Unit)? = null
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleDown else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "iosBounceScale"
    )

    this
        .scale(scale)
        .then(
            if (onClick != null) {
                Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
            } else Modifier
        )
}

/**
 * Pulsing subtle breathing glow for active tracking elements (timers, flame, live tracker).
 */
fun Modifier.pulsingGlow(
    color: Color = Color(0xFF2447F5)
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    this.shadow(
        elevation = 10.dp,
        shape = CircleShape,
        ambientColor = color.copy(alpha = pulseAlpha * 0.4f),
        spotColor = color.copy(alpha = pulseAlpha)
    )
}

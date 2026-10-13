package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.example.ui.theme.PulseLine
import com.example.ui.theme.PulseSurface

/**
 * Standard PulseHabit Card modifier adhering strictly to design.md:
 * Flat solid surface (#F5F6F8), 12dp corner radius, and 1dp crisp line border (#DDE1E8).
 * No blur, no glassmorphism, no translucent panels.
 */
fun Modifier.pulseCard(
    shape: Shape = RoundedCornerShape(12.dp),
    backgroundColor: Color = PulseSurface,
    borderColor: Color = PulseLine
): Modifier = this
    .clip(shape)
    .background(backgroundColor)
    .border(BorderStroke(1.dp, borderColor), shape)


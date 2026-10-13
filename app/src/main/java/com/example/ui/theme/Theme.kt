package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val PulseColorScheme = lightColorScheme(
    primary = PulsePrimary,
    onPrimary = PulseOnPrimary,
    primaryContainer = PulseSurfaceContainer,
    onPrimaryContainer = PulseInk,
    secondary = PulseStreak,
    onSecondary = PulseOnPrimary,
    background = PulseBg,
    onBackground = PulseInk,
    surface = PulseSurface,
    onSurface = PulseInk,
    surfaceVariant = PulseSurfaceStrong,
    onSurfaceVariant = PulseInkMuted,
    outline = PulseLine,
    error = PulseError,
    onError = PulseOnPrimary,
    errorContainer = PulseErrorContainer,
    onErrorContainer = PulseError
)

@Composable
fun PulseHabitTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PulseColorScheme,
        typography = Typography,
        content = content
    )
}

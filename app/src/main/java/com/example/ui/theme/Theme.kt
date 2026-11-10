package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val GutiverseDarkColorScheme = darkColorScheme(
    primary = GutiverseGold,
    onPrimary = GutiverseDarkVoid,
    primaryContainer = GutiverseSurfaceElevated,
    onPrimaryContainer = GutiverseGold,
    secondary = GutiverseCyan,
    onSecondary = GutiverseDarkVoid,
    secondaryContainer = GutiverseSurfaceElevated,
    onSecondaryContainer = GutiverseCyan,
    tertiary = GutiverseLaserCrimson,
    onTertiary = GutiverseTextPrimary,
    background = GutiverseDarkVoid,
    onBackground = GutiverseTextPrimary,
    surface = GutiverseSurfaceDark,
    onSurface = GutiverseTextPrimary,
    surfaceVariant = GutiverseSurfaceElevated,
    onSurfaceVariant = GutiverseTextSecondary,
    outline = GutiverseSurfaceBorder
)

@Composable
fun GutiverseTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GutiverseDarkColorScheme,
        typography = Typography,
        content = content
    )
}

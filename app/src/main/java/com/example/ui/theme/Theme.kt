package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NeonViolet,
    onPrimary = Color.Black,
    primaryContainer = NeonVioletDark,
    onPrimaryContainer = NeonVioletLight,
    secondary = NeonCobalt,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF0F3D5E),
    onSecondaryContainer = NeonCobaltLight,
    tertiary = NeonVioletLight,
    onTertiary = Color.Black,
    background = ObsidianBackground,
    onBackground = TextWhite,
    surface = ObsidianSurface,
    onSurface = TextWhite,
    surfaceVariant = ObsidianCard,
    onSurfaceVariant = TextMuted,
    outline = ObsidianCardBorder,
    outlineVariant = Color(0xFF1E1E36),
    error = NeonError,
    onError = Color.Black
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Futuristic dark theme default
    dynamicColor: Boolean = false, // Keep high-contrast neon branding
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

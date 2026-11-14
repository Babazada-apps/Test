package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.data.repository.AppThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = AviationNavyDark,
    onPrimary = Color(0xFF0F172A),
    primaryContainer = AviationNavyContainerDark,
    onPrimaryContainer = Color(0xFFE2E8F0),
    secondary = AviationSkyBlueDark,
    onSecondary = Color(0xFF0C4A6E),
    secondaryContainer = AviationSkyBlueContainerDark,
    onSecondaryContainer = Color(0xFFE0F2FE),
    tertiary = AviationGoldDark,
    onTertiary = Color(0xFF451A03),
    tertiaryContainer = AviationGoldContainerDark,
    onTertiaryContainer = Color(0xFFFEF3C7),
    background = AviationBackgroundDark,
    onBackground = Color(0xFFF1F5F9),
    surface = AviationSurfaceDark,
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFF94A3B8)
)

private val LightColorScheme = lightColorScheme(
    primary = AviationNavyPrimary,
    onPrimary = Color.White,
    primaryContainer = AviationNavyContainer,
    onPrimaryContainer = Color(0xFF0F172A),
    secondary = AviationSkyBlue,
    onSecondary = Color.White,
    secondaryContainer = AviationSkyBlueContainer,
    onSecondaryContainer = Color(0xFF0369A1),
    tertiary = AviationGold,
    onTertiary = Color.White,
    tertiaryContainer = AviationGoldContainer,
    onTertiaryContainer = Color(0xFFB45309),
    background = AviationBackgroundLight,
    onBackground = Color(0xFF0F172A),
    surface = AviationSurfaceLight,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF64748B)
)

@Composable
fun FlightOpsTheme(
    appThemeMode: AppThemeMode = AppThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val isDark = when (appThemeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

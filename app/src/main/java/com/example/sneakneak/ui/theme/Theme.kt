package com.example.sneakneak.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Centralized theme mapping for all Compose screens.
// UI files should consume semantic colors from MaterialTheme/AppColors
// instead of hardcoding visual tokens locally.
private val LightColorScheme = lightColorScheme(
    primary = AppColors.Primary,
    onPrimary = AppColors.OnPrimary,
    background = AppColors.Background,
    onBackground = AppColors.TextPrimary,
    surface = AppColors.Surface,
    onSurface = AppColors.TextPrimary,
    surfaceVariant = AppColors.SurfaceVariant,
    onSurfaceVariant = AppColors.TextSecondary,
    outline = AppColors.Border,
    error = AppColors.Error,
    onError = AppColors.OnPrimary,
)

private val DarkColorScheme = darkColorScheme(
    primary = AppColors.PrimaryLight,
    onPrimary = AppColors.TextPrimary,
    background = AppColors.TextPrimary,
    onBackground = AppColors.Surface,
    surface = AppColors.PrimaryDark,
    onSurface = AppColors.Surface,
    surfaceVariant = AppColors.PrimaryDark,
    onSurfaceVariant = AppColors.SurfaceVariant,
    outline = AppColors.TextSecondary,
    error = AppColors.Error,
)

@Composable
fun AppTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content,
    )
}

@Composable
fun SneakneakTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    // Kept as a compatibility wrapper while the project converges on AppTheme.
    AppTheme(
        darkTheme = darkTheme,
        content = content,
    )
}

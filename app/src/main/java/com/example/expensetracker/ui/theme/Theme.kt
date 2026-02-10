package com.example.expensetracker.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

interface ThemeManager {
    val isDark: Boolean
    fun toggleTheme()
}

val LocalThemeManager = staticCompositionLocalOf<ThemeManager> {
    error("No ThemeManager provided")
}

private val LightLivelyColorScheme = lightColorScheme(
    primary = BrandPurple,
    onPrimary = Color.White,
    background = AppBackground,
    surface = CardBackground,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outlineVariant = DividerColor,
    error = ExpenseRed,
    tertiary = IncomeGreen
)

private val DarkLivelyColorScheme = darkColorScheme(
    primary = BrandPurpleLight,
    onPrimary = Color.Black,
    background = AppBackgroundDark,
    surface = CardBackgroundDark,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    onSurfaceVariant = TextSecondaryDark,
    outlineVariant = DividerColorDark,
    error = ExpenseRed,
    tertiary = IncomeGreen
)

@Composable
fun ExpenseTrackerTheme(
    darkTheme: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkLivelyColorScheme else LightLivelyColorScheme
    val view = LocalView.current

    val themeManager = remember(darkTheme) {
        object : ThemeManager {
            override val isDark = darkTheme
            override fun toggleTheme() = onToggle()
        }
    }

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()

            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    CompositionLocalProvider(LocalThemeManager provides themeManager) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
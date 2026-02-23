package com.dve.sari.todolistapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFFE0E0E0), // Light gray for emphasis
    secondary = androidx.compose.ui.graphics.Color(0xFFBDBDBD),
    tertiary = androidx.compose.ui.graphics.Color(0xFF9E9E9E),
    background = androidx.compose.ui.graphics.Color(0xFF121212), // Almost black
    surface = androidx.compose.ui.graphics.Color(0xFF1E1E1E), // Dark gray
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFF2C2C2C),
    onPrimary = androidx.compose.ui.graphics.Color(0xFF121212),
    onSecondary = androidx.compose.ui.graphics.Color(0xFF121212),
    onTertiary = androidx.compose.ui.graphics.Color(0xFF121212),
    onBackground = androidx.compose.ui.graphics.Color(0xFFE0E0E0),
    onSurface = androidx.compose.ui.graphics.Color(0xFFE0E0E0),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFFBDBDBD),
    primaryContainer = androidx.compose.ui.graphics.Color(0xFF2C2C2C),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFFE0E0E0),
    secondaryContainer = androidx.compose.ui.graphics.Color(0xFF252525),
    onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFFBDBDBD)
)

private val LightColorScheme = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF424242), // Dark gray for emphasis
    secondary = androidx.compose.ui.graphics.Color(0xFF616161),
    tertiary = androidx.compose.ui.graphics.Color(0xFF757575),
    background = androidx.compose.ui.graphics.Color(0xFFF5F5F5), // Off-white
    surface = androidx.compose.ui.graphics.Color.White,
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFFFAFAFA),
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    onBackground = androidx.compose.ui.graphics.Color(0xFF212121),
    onSurface = androidx.compose.ui.graphics.Color(0xFF212121),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF616161),
    primaryContainer = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFF212121),
    secondaryContainer = androidx.compose.ui.graphics.Color(0xFFF5F5F5),
    onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFF424242)
)

@Composable
fun CoolWeatherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled for consistent branding
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = if (darkTheme) {
                android.graphics.Color.TRANSPARENT
            } else {
                android.graphics.Color.TRANSPARENT
            }
            window.navigationBarColor = if (darkTheme) {
                Gray900.toArgb()
            } else {
                androidx.compose.ui.graphics.Color.White.toArgb()
            }

            val windowInsetsController = WindowCompat.getInsetsController(window, view)
            windowInsetsController.isAppearanceLightStatusBars = !darkTheme
            windowInsetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
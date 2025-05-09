package com.example.ndejjenews.ui.theme

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

// Define the Light Color Scheme
private val LightColorScheme = lightColorScheme(
    primary = NdejjeBlue,
    onPrimary = Gray10,
    primaryContainer = NdejjeBlueLight,
    onPrimaryContainer = NdejjeBlueDark,
    secondary = NdejjeYellow,
    onSecondary = Gray95,
    secondaryContainer = NdejjeGold,
    onSecondaryContainer = Gray95,
    tertiary = NdejjeGold,
    onTertiary = Gray95,
    background = Gray10,
    onBackground = Gray95,
    surface = Gray10,
    onSurface = Gray95,
    surfaceVariant = Gray20,
    onSurfaceVariant = Gray80,
    error = ErrorColor,
    onError = Gray10
)

// Define the Dark Color Scheme
private val DarkColorScheme = darkColorScheme(
    primary = DarkNdejjeBlue,
    onPrimary = Gray10,
    primaryContainer = DarkNdejjeBlueDark,
    onPrimaryContainer = DarkNdejjeBlueLight,
    secondary = DarkNdejjeYellow,
    onSecondary = Gray95,
    secondaryContainer = DarkNdejjeGold,
    onSecondaryContainer = Gray95,
    tertiary = DarkNdejjeGold,
    onTertiary = Gray95,
    background = Gray98,
    onBackground = Gray20,
    surface = Gray95,
    onSurface = Gray20,
    surfaceVariant = Gray90,
    onSurfaceVariant = Gray40,
    error = DarkErrorColor,
    onError = Gray20
)

@Composable
fun NdejjeNewsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Set to false to use our custom colors
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
    
    // Set the status bar color based on theme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
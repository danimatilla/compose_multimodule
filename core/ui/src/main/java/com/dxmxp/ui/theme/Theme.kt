package com.dxmxp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF95E6CB),
    onPrimary = Color(0xFF0D1F1A),
    secondary = Color(0xFF7ED3B4),
    onSecondary = Color(0xFF0D1F1A),
    tertiary = Color(0xFFC9F4E5),
    background = Color(0xFFF6FFFB),
    onBackground = Color(0xFF11261F),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF11261F),
    surfaceVariant = Color(0xFFEAFBF5),
    outline = Color(0xFFB7E8D7)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF09244B),
    onPrimary = Color(0xFFEAF2FF),
    secondary = Color(0xFF1A3D72),
    onSecondary = Color(0xFFEAF2FF),
    tertiary = Color(0xFF3E6AA4),
    background = Color(0xFF071A2E),
    onBackground = Color(0xFFEAF2FF),
    surface = Color(0xFF0D2340),
    onSurface = Color(0xFFEAF2FF),
    surfaceVariant = Color(0xFF102B4D),
    outline = Color(0xFF2B4E7A)
)

@Composable
fun SeedTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

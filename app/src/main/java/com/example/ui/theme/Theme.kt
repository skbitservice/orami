package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = OramiWhite,
    onPrimary = OramiBlack,
    primaryContainer = OramiDarkCard,
    onPrimaryContainer = OramiWhite,
    secondary = OramiCoral,
    onSecondary = OramiWhite,
    secondaryContainer = OramiCharcoal,
    onSecondaryContainer = OramiWhite,
    tertiary = OramiEmerald,
    background = OramiDarkSurface,
    onBackground = OramiWhite,
    surface = OramiDarkCard,
    onSurface = OramiWhite,
    surfaceVariant = OramiCharcoal,
    onSurfaceVariant = OramiTextMuted,
    outline = OramiCharcoal
)

private val LightColorScheme = lightColorScheme(
    primary = OramiBlack,
    onPrimary = OramiWhite,
    primaryContainer = OramiSurfaceVariant,
    onPrimaryContainer = OramiBlack,
    secondary = OramiCoral,
    onSecondary = OramiWhite,
    secondaryContainer = OramiCoralLight,
    onSecondaryContainer = OramiCoral,
    tertiary = OramiEmerald,
    background = OramiBackground,
    onBackground = OramiTextPrimary,
    surface = OramiSurface,
    onSurface = OramiTextPrimary,
    surfaceVariant = OramiSurfaceVariant,
    onSurfaceVariant = OramiTextSecondary,
    outline = OramiBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep intentional brand colors consistent
    content: @Composable () -> Unit,
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

package com.tapp.recordingai.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color(0xFF0D47A1),
    primaryContainer = Color(0xFF1565C0),
    onPrimaryContainer = Color(0xFFE3F2FD),
    secondary = Color(0xFF64B5F6),
    onSecondary = Color.Black,
    tertiary = Color(0xFF42A5F5),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    surfaceVariant = Color(0xFF252F3D),
    onBackground = Color(0xFFECECEC),
    onSurface = Color(0xFFECECEC),
    onSurfaceVariant = Color(0xFFB0BEC5),
    outline = Color(0xFF546E7A)
)

private val LightColorScheme = lightColorScheme(
    primary = AppPrimary,
    onPrimary = Color.White,
    primaryContainer = AppPrimaryContainer,
    onPrimaryContainer = AppPrimaryDark,
    secondary = AppPrimaryDark,
    onSecondary = Color.White,
    tertiary = Color(0xFF90CAF9),
    background = Color.White,
    surface = Color.White,
    surfaceVariant = AppSurfaceNote,
    onBackground = Color(0xFF212121),
    onSurface = Color(0xFF212121),
    onSurfaceVariant = Color(0xFF757575),
    outline = AppOutlineBlue,
    outlineVariant = AppPrimaryContainer
)

@Composable
fun RecordingAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
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
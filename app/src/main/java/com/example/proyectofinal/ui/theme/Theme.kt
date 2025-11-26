package com.example.proyectofinal.ui.theme

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = RedDominant,
    onPrimary = White,
    primaryContainer = DarkRed,
    onPrimaryContainer = White,
    secondary = GrayDark,
    onSecondary = White,
    tertiary = DarkRed,
    background = Color(0xFF121212), // Fondo negro/gris muy oscuro para reducir fatiga visual
    onBackground = White,
    surface = Color(0xFF1E1E1E), // Superficies un poco más claras que el fondo
    onSurface = White,
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFDDDDDD), // Texto secundario más legible en oscuro
    error = Color(0xFFCF6679), // Color de error estándar para modo oscuro (más suave que el rojo puro)
    onError = BlackPure
)

private val LightColorScheme = lightColorScheme(
    primary = RedDominant,
    onPrimary = White,
    primaryContainer = RedDominant,
    onPrimaryContainer = White,
    secondary = GrayDark,
    onSecondary = White,
    tertiary = DarkRed,
    background = GrayLight,
    onBackground = BlackPure,
    surface = White,
    onSurface = BlackPure,
    surfaceVariant = White,
    onSurfaceVariant = GrayDark,
    error = RedDominant,
    onError = White
)

@Composable
fun ProyectofinalTheme(
    // Permitimos pasar un parámetro explícito para controlar el modo oscuro
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
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb() // Barra de estado coincide con el fondo
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme // Iconos oscuros solo en tema claro
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
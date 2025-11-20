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
    primaryContainer = DarkRed, // Un rojo más oscuro para contenedores en modo oscuro
    onPrimaryContainer = White,
    secondary = GrayDark,
    onSecondary = White,
    tertiary = DarkRed,
    background = BlackPure,
    onBackground = White,
    surface = DarkRed, // Superficies en modo oscuro (tarjetas, etc)
    onSurface = White,
    surfaceVariant = Color(0xFF2C0000), // Un poco más claro que DarkRed para inputs
    onSurfaceVariant = GrayLight,
    error = RedDominant,
    onError = White
)

private val LightColorScheme = lightColorScheme(
    primary = RedDominant,
    onPrimary = White,
    primaryContainer = RedDominant, // Para que el TopAppBar sea rojo
    onPrimaryContainer = White,
    secondary = GrayDark,
    onSecondary = White,
    tertiary = DarkRed,
    background = GrayLight,
    onBackground = BlackPure,
    surface = White,
    onSurface = BlackPure,
    surfaceVariant = White, // Campos de texto blancos
    onSurfaceVariant = GrayDark, // Placeholder text y bordes
    error = RedDominant,
    onError = White
)

@Composable
fun ProyectofinalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Desactivamos dynamicColor para forzar nuestra paleta
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
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false // Status bar icons white (since primary is dark red)
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
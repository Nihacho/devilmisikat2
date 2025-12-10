package com.example.proyectofinal.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.example.proyectofinal.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

// Definición de Playfair Display (Serif)
// Definición de Outfit (Sans-Serif Moderno)
val OutfitFamily = FontFamily(
    Font(googleFont = GoogleFont("Outfit"), fontProvider = provider, weight = FontWeight.Black), // 900
    Font(googleFont = GoogleFont("Outfit"), fontProvider = provider, weight = FontWeight.Bold), // 700
    Font(googleFont = GoogleFont("Outfit"), fontProvider = provider, weight = FontWeight.SemiBold), // 600
    Font(googleFont = GoogleFont("Outfit"), fontProvider = provider, weight = FontWeight.Medium), // 500
    Font(googleFont = GoogleFont("Outfit"), fontProvider = provider, weight = FontWeight.Normal) // 400
)

// Definición de Lato (Sans-Serif)
val LatoFamily = FontFamily(
    Font(googleFont = GoogleFont("Lato"), fontProvider = provider, weight = FontWeight.Bold), // 700
    Font(googleFont = GoogleFont("Lato"), fontProvider = provider, weight = FontWeight.Medium), // 500
    Font(googleFont = GoogleFont("Lato"), fontProvider = provider, weight = FontWeight.Normal) // 400
)

val Typography = Typography(
    // Título Principal de Pantalla: "Sing In"
    // Título Principal de Pantalla
    headlineLarge = TextStyle(
        fontFamily = OutfitFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.5).sp
    ),
    // Título del Logo: "DEVILMISIKAT"
    titleLarge = TextStyle(
        fontFamily = OutfitFamily,
        fontWeight = FontWeight.Black,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = 1.sp
    ),
    // Botones de Acción: "Sing In", "Sing Up"
    // Botones de Acción
    labelLarge = TextStyle(
        fontFamily = OutfitFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp
    ),
    // Subtítulos destacados
    headlineMedium = TextStyle(
        fontFamily = OutfitFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    // Texto de Campos (Input)
    bodyLarge = TextStyle(
        fontFamily = LatoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.25.sp
    ),
    // Placeholder (Pistas) y Subtítulos suaves
    bodyMedium = TextStyle(
        fontFamily = LatoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    // Enlaces y Texto de Ayuda: "Forgot your password?"
    labelSmall = TextStyle(
        fontFamily = LatoFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
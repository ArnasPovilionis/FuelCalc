package com.example.fuelcalc.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Paletė: gilus mėlynas (pagrindinė), oranžinė (akcentas, „kuras“), turkio (papildoma)
private val LightColors = lightColorScheme(
    primary = Color(0xFF1E3A8A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCE4FF),
    onPrimaryContainer = Color(0xFF0B1B4D),
    secondary = Color(0xFFF97316),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE4D1),
    onSecondaryContainer = Color(0xFF4A1D00),
    tertiary = Color(0xFF0E9F9A),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFCCF3F0),
    onTertiaryContainer = Color(0xFF00302E),
    background = Color(0xFFF4F6FB),
    onBackground = Color(0xFF151A26),
    surface = Color(0xFFF4F6FB),
    onSurface = Color(0xFF151A26),
    surfaceVariant = Color(0xFFE3E7F1),
    onSurfaceVariant = Color(0xFF4A5164),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFFFFFFF),
    surfaceContainer = Color(0xFFFFFFFF),
    surfaceContainerHigh = Color(0xFFEDF0F7),
    surfaceContainerHighest = Color(0xFFE6EAF3),
    outline = Color(0xFF8A91A5),
    outlineVariant = Color(0xFFD3D8E4)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF9DB4FF),
    onPrimary = Color(0xFF0B1B4D),
    primaryContainer = Color(0xFF233B7A),
    onPrimaryContainer = Color(0xFFDCE4FF),
    secondary = Color(0xFFFFA866),
    onSecondary = Color(0xFF4A1D00),
    secondaryContainer = Color(0xFF7A3510),
    onSecondaryContainer = Color(0xFFFFE4D1),
    tertiary = Color(0xFF5EDAD3),
    onTertiary = Color(0xFF00302E),
    tertiaryContainer = Color(0xFF005450),
    onTertiaryContainer = Color(0xFFCCF3F0),
    background = Color(0xFF0E121C),
    onBackground = Color(0xFFE3E6EF),
    surface = Color(0xFF0E121C),
    onSurface = Color(0xFFE3E6EF),
    surfaceVariant = Color(0xFF2A3040),
    onSurfaceVariant = Color(0xFFBFC5D6),
    surfaceContainerLowest = Color(0xFF0A0D15),
    surfaceContainerLow = Color(0xFF161B27),
    surfaceContainer = Color(0xFF1A202D),
    surfaceContainerHigh = Color(0xFF232A39),
    surfaceContainerHighest = Color(0xFF2C3444),
    outline = Color(0xFF8A91A5),
    outlineVariant = Color(0xFF353C4D)
)

private val AppTypography = Typography().run {
    copy(
        displaySmall = displaySmall.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.5).sp),
        headlineMedium = headlineMedium.copy(fontWeight = FontWeight.Bold),
        headlineSmall = headlineSmall.copy(fontWeight = FontWeight.Bold),
        titleLarge = titleLarge.copy(fontWeight = FontWeight.Bold),
        titleMedium = titleMedium.copy(fontWeight = FontWeight.SemiBold),
        labelLarge = labelLarge.copy(fontWeight = FontWeight.SemiBold),
        labelSmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
            letterSpacing = 0.8.sp
        )
    )
}

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun FuelCalcTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}

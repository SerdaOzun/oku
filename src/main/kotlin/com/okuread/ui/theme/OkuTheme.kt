package com.okuread.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

@Composable
fun OkuTheme(
    appTheme: AppTheme,
    content: @Composable () -> Unit
) {
    val pastelColors = lightColors(
        primary = Color(0xFFB3E5FC),          // Light Blue
        primaryVariant = Color(0xFF81D4FA),   // Slightly Darker Light Blue
        secondary = Color(0xFFFFF9C4),        // Light Yellow
        secondaryVariant = Color(0xFFFFF176), // Slightly Darker Light Yellow
        background = Color(0xFFFFF8E1),       // Very Light Cream
        surface = Color(0xFFFFF8E1),          // Very Light Cream
        error = Color(0xFFFFCDD2),            // Light Red
        onPrimary = Color(0xFF000000),        // Black for contrast
        onSecondary = Color(0xFF000000),      // Black for contrast
        onBackground = Color(0xFF000000),     // Black for contrast
        onSurface = Color(0xFF000000),        // Black for contrast
        onError = Color(0xFF000000)           // Black for contrast
    )
    val moodyPastelColors = lightColors(
        primary = Color(0xFF9575CD),          // Soft Purple
        primaryVariant = Color(0xFF7E57C2),   // Darker Soft Purple
        secondary = Color(0xFF4DB6AC),        // Soft Teal
        secondaryVariant = Color(0xFF00897B), // Darker Soft Teal
        background = Color(0xFF424242),       // Dark Gray
        surface = Color(0xFF616161),          // Slightly Lighter Gray
        error = Color(0xFFEF5350),            // Soft Red
        onPrimary = Color(0xFFFFFFFF),        // White for contrast
        onSecondary = Color(0xFFFFFFFF),      // White for contrast
        onBackground = Color(0xFFFFFFFF),     // White for contrast
        onSurface = Color(0xFFFFFFFF),        // White for contrast
        onError = Color(0xFFFFFFFF)           // White for contrast
    )
    val lightAiryPastelColors = lightColors(
        primary = Color(0xFF64B5F6),          // Sky Blue
        primaryVariant = Color(0xFF2196F3),   // Darker Sky Blue
        secondary = Color(0xFFFFCC80),        // Peach
        secondaryVariant = Color(0xFFFFB74D), // Darker Peach
        background = Color(0xFFFFFFFF),       // White
        surface = Color(0xFFFFFFFF),          // White
        error = Color(0xFFEF5350),            // Soft Red
        onPrimary = Color(0xFF000000),        // Black for contrast
        onSecondary = Color(0xFF000000),      // Black for contrast
        onBackground = Color(0xFF000000),     // Black for contrast
        onSurface = Color(0xFF000000),        // Black for contrast
        onError = Color(0xFFFFFFFF)           // White for contrast
    )
    val colors = if (appTheme == AppTheme.DARK) {
        darkColors()
    } else {
        lightAiryPastelColors
    }
//    val ibmCarbonFont = FontFamily(
//        Font("font/IBMPlexSans-Bold.ttf", weight = FontWeight.Bold, style = FontStyle.Normal),
//        Font("font/IBMPlexSans-Italic.ttf", style = FontStyle.Italic),
//        Font("font/IBMPlexSans-Regular.ttf", style = FontStyle.Normal)
//    )

//    val shapes = Shapes(
//        small = RoundedCornerShape(30.dp),
//        medium = RoundedCornerShape(10.dp),
//        large = RoundedCornerShape(0.dp)
//    )

    CompositionLocalProvider(LocalSpacing provides Spacing(), LocalFontSize provides FontSize()) {
        MaterialTheme(
            colors = colors,
            content = content
        )
    }

}

enum class AppTheme(val label: String) {
    LIGHT("Light"), DARK("Dark")
}
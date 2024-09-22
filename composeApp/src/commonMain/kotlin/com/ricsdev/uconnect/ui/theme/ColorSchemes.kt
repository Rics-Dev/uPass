package com.ricsdev.uconnect.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val LightColorScheme = lightColorScheme(
    primary = Color(0xFF000000), // Black
    onPrimary = Color.White, // White text on black
    primaryContainer = Color(0xFFBDBDBD), // Light gray container
    onPrimaryContainer = Color(0xFF212121), // Darker gray text on light gray
    secondary = Color(0xFF616161), // Medium gray
    onSecondary = Color.White, // White text on medium gray
    secondaryContainer = Color(0xFFE0E0E0), // Light gray container
    onSecondaryContainer = Color(0xFF424242), // Dark gray text on light gray
    tertiary = Color(0xFF757575), // Another shade of gray
    onTertiary = Color.White, // White text on gray
    tertiaryContainer = Color(0xFFEEEEEE), // Lighter gray container
    onTertiaryContainer = Color(0xFF616161), // Medium gray text on light gray
    background = Color.White, // White background
    onBackground = Color.Black, // Black text on white background
    surface = Color.White, // White surface
    onSurface = Color.Black, // Black text on white surface
    surfaceVariant = Color(0xFFE0E0E0), // Light gray variant surface
    onSurfaceVariant = Color(0xFF424242), // Dark gray text on light gray surface
    error = Color(0xFFB00020), // Default error color
    onError = Color.White, // White text on error
    errorContainer = Color(0xFFFCDAD7), // Light red error container
    onErrorContainer = Color(0xFF410002), // Dark text on error container
    outline = Color(0xFFBDBDBD) // Gray outline
)

val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFFFFF), // White
    onPrimary = Color.Black, // Black text on white
    primaryContainer = Color(0xFF424242), // Dark gray container
    onPrimaryContainer = Color(0xFFE0E0E0), // Light gray text on dark gray
    secondary = Color(0xFFBDBDBD), // Light gray
    onSecondary = Color.Black, // Black text on light gray
    secondaryContainer = Color(0xFF616161), // Medium gray container
    onSecondaryContainer = Color(0xFFE0E0E0), // Light gray text on medium gray
    tertiary = Color(0xFF9E9E9E), // Gray
    onTertiary = Color.Black, // Black text on gray
    tertiaryContainer = Color(0xFF424242), // Dark gray container
    onTertiaryContainer = Color(0xFFE0E0E0), // Light gray text on dark gray
    background = Color(0xFF121212), // Almost black background
    onBackground = Color.White, // White text on dark background
    surface = Color(0xFF121212), // Almost black surface
    onSurface = Color.White, // White text on dark surface
    surfaceVariant = Color(0xFF424242), // Dark gray variant surface
    onSurfaceVariant = Color(0xFFBDBDBD), // Light gray text on dark surface
    error = Color(0xFFCF6679), // Default dark error color
    onError = Color.Black, // Black text on error
    errorContainer = Color(0xFF93000A), // Dark red error container
    onErrorContainer = Color(0xFFFFDAD6), // Light text on error container
    outline = Color(0xFF616161) // Gray outline
)
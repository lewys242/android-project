package com.mbongo.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Gold,
    onPrimary = Black,
    primaryContainer = DarkGray,
    onPrimaryContainer = Gold,
    
    secondary = LightGold,
    onSecondary = Black,
    secondaryContainer = MediumGray,
    onSecondaryContainer = LightGold,
    
    tertiary = Success,
    onTertiary = Black,
    
    background = Black,
    onBackground = White,
    
    surface = DarkGray,
    onSurface = White,
    surfaceVariant = MediumGray,
    onSurfaceVariant = LightGray,
    
    error = Error,
    onError = White,
    
    outline = Gold,
    outlineVariant = MediumGray
)

@Composable
fun MbongoTheme(
    darkTheme: Boolean = true, // Always dark theme for Mbongo
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

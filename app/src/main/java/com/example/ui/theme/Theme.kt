package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = EmeraldOnPrimary,
    primaryContainer = EmeraldPrimaryContainer,
    onPrimaryContainer = EmeraldOnPrimaryContainer,
    secondary = EmeraldSecondary,
    onSecondary = EmeraldOnSecondary,
    secondaryContainer = EmeraldSecondaryContainer,
    onSecondaryContainer = EmeraldOnSecondaryContainer,
    tertiary = GoldAccent,
    onTertiary = GoldOnAccent,
    tertiaryContainer = GoldAccentLight,
    onTertiaryContainer = GoldAccentDark,
    background = CreamBackground,
    onBackground = TextDark,
    surface = CreamSurface,
    onSurface = TextDark,
    surfaceVariant = CreamSurfaceVariant,
    onSurfaceVariant = TextMuted,
    outline = Color(0xFFD6D3D1)
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkEmeraldPrimary,
    onPrimary = DarkEmeraldOnPrimary,
    primaryContainer = EmeraldPrimary,
    onPrimaryContainer = EmeraldPrimaryContainer,
    secondary = EmeraldSecondary,
    onSecondary = Color.White,
    secondaryContainer = DarkEmeraldSurfaceVariant,
    onSecondaryContainer = DarkEmeraldPrimary,
    tertiary = DarkGoldAccent,
    onTertiary = Color.Black,
    tertiaryContainer = GoldAccentDark,
    onTertiaryContainer = GoldAccentLight,
    background = DarkEmeraldBackground,
    onBackground = DarkTextLight,
    surface = DarkEmeraldSurface,
    onSurface = DarkTextLight,
    surfaceVariant = DarkEmeraldSurfaceVariant,
    onSurfaceVariant = DarkTextMuted,
    outline = Color(0xFF334E46)
)

@Composable
fun NoorDailyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}

// Backwards compatibility alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    NoorDailyTheme(darkTheme = darkTheme, content = content)
}

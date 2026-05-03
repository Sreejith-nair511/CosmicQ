package com.example.cosmoq1.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CosmicColorScheme = darkColorScheme(
    primary = SpaceCyan,
    onPrimary = SpaceBlack,
    primaryContainer = SpaceAccentBlue,
    onPrimaryContainer = StarWhite,
    secondary = SpacePurpleLight,
    onSecondary = SpaceBlack,
    secondaryContainer = SpacePurple,
    onSecondaryContainer = StarWhite,
    tertiary = SpaceGold,
    onTertiary = SpaceBlack,
    background = SpaceBlack,
    onBackground = StarWhite,
    surface = SurfaceDark,
    onSurface = StarWhite,
    surfaceVariant = CardBackground,
    onSurfaceVariant = StarWhite,
    outline = CardBorder,
    error = SpaceRed,
    onError = StarWhite
)

@Composable
fun CosmicExplorerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CosmicColorScheme,
        typography = Typography,
        content = content
    )
}

// Keep old name as alias so existing references don't break
@Composable
fun COSMOQ1Theme(content: @Composable () -> Unit) = CosmicExplorerTheme(content)

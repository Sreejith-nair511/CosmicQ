package com.example.cosmoq1.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

enum class AppTheme { DEEP_SPACE, GALAXY_PURPLE, SOLAR_GOLD }

val LocalAppTheme = staticCompositionLocalOf { AppTheme.DEEP_SPACE }

private val DeepSpaceScheme = darkColorScheme(
    primary            = DS_Primary,
    onPrimary          = SpaceBlack,
    primaryContainer   = DS_Border,
    onPrimaryContainer = StarWhite,
    secondary          = DS_Secondary,
    onSecondary        = StarWhite,
    secondaryContainer = DS_Card,
    onSecondaryContainer = StarWhite,
    tertiary           = DS_Tertiary,
    onTertiary         = SpaceBlack,
    background         = DS_Background,
    onBackground       = StarWhite,
    surface            = DS_Surface,
    onSurface          = StarWhite,
    surfaceVariant     = DS_Card,
    onSurfaceVariant   = StarWhite,
    outline            = DS_Border,
    error              = SpaceRed,
    onError            = StarWhite
)

private val GalaxyPurpleScheme = darkColorScheme(
    primary            = GP_Primary,
    onPrimary          = SpaceBlack,
    primaryContainer   = GP_Border,
    onPrimaryContainer = StarWhite,
    secondary          = GP_Secondary,
    onSecondary        = StarWhite,
    secondaryContainer = GP_Card,
    onSecondaryContainer = StarWhite,
    tertiary           = GP_Tertiary,
    onTertiary         = SpaceBlack,
    background         = GP_Background,
    onBackground       = StarWhite,
    surface            = GP_Surface,
    onSurface          = StarWhite,
    surfaceVariant     = GP_Card,
    onSurfaceVariant   = StarWhite,
    outline            = GP_Border,
    error              = SpaceRed,
    onError            = StarWhite
)

private val SolarGoldScheme = darkColorScheme(
    primary            = SG_Primary,
    onPrimary          = SpaceBlack,
    primaryContainer   = SG_Border,
    onPrimaryContainer = StarWhite,
    secondary          = SG_Secondary,
    onSecondary        = SpaceBlack,
    secondaryContainer = SG_Card,
    onSecondaryContainer = StarWhite,
    tertiary           = SG_Tertiary,
    onTertiary         = StarWhite,
    background         = SG_Background,
    onBackground       = StarWhite,
    surface            = SG_Surface,
    onSurface          = StarWhite,
    surfaceVariant     = SG_Card,
    onSurfaceVariant   = StarWhite,
    outline            = SG_Border,
    error              = SpaceRed,
    onError            = StarWhite
)

@Composable
fun CosmicSwipeTheme(
    appTheme: AppTheme = AppTheme.DEEP_SPACE,
    content: @Composable () -> Unit
) {
    val colorScheme = when (appTheme) {
        AppTheme.DEEP_SPACE     -> DeepSpaceScheme
        AppTheme.GALAXY_PURPLE  -> GalaxyPurpleScheme
        AppTheme.SOLAR_GOLD     -> SolarGoldScheme
    }
    CompositionLocalProvider(LocalAppTheme provides appTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = Typography,
            content     = content
        )
    }
}

// Legacy aliases so existing screens still compile
@Composable
fun CosmicExplorerTheme(content: @Composable () -> Unit) = CosmicSwipeTheme(content = content)

@Composable
fun COSMOQ1Theme(content: @Composable () -> Unit) = CosmicSwipeTheme(content = content)

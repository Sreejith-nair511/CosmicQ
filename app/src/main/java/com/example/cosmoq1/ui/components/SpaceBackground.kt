package com.example.cosmoq1.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.cosmoq1.ui.theme.SpaceBlack
import com.example.cosmoq1.ui.theme.SpaceDeepBlue
import com.example.cosmoq1.ui.theme.SpaceMidBlue

@Composable
fun SpaceBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        SpaceMidBlue,
                        SpaceDeepBlue,
                        SpaceBlack
                    ),
                    center = Offset(0.3f, 0.2f),
                    radius = 1800f
                )
            ),
        content = content
    )
}

@Composable
fun SpaceGradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SpaceDeepBlue,
                        SpaceBlack,
                        Color(0xFF050510)
                    )
                )
            ),
        content = content
    )
}

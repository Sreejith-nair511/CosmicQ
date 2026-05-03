package com.example.cosmoq1.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.cosmoq1.ui.theme.SpaceBlack
import com.example.cosmoq1.ui.theme.SpaceDeepBlue
import com.example.cosmoq1.ui.theme.SpaceMidBlue
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun SpaceBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(SpaceMidBlue, SpaceDeepBlue, SpaceBlack),
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
                Brush.verticalGradient(
                    colors = listOf(SpaceDeepBlue, SpaceBlack, Color(0xFF050510))
                )
            ),
        content = content
    )
}

data class StarParticle(val x: Float, val y: Float, val r: Float, val baseAlpha: Float, val phase: Float)

@Composable
fun AnimatedStarField(modifier: Modifier = Modifier, bgColor: Color = Color(0xFF000010)) {
    val stars = remember {
        List(100) {
            StarParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                r = Random.nextFloat() * 2.2f + 0.4f,
                baseAlpha = Random.nextFloat() * 0.6f + 0.3f,
                phase = Random.nextFloat() * 6.28f
            )
        }
    }
    val inf = rememberInfiniteTransition(label = "stars")
    val t by inf.animateFloat(
        initialValue = 0f, targetValue = 6.28f,
        animationSpec = infiniteRepeatable(tween(5000, easing = LinearEasing)),
        label = "t"
    )
    Canvas(modifier = modifier.fillMaxSize().background(bgColor)) {
        stars.forEach { s ->
            val a = s.baseAlpha * (sin(t + s.phase).toFloat() * 0.3f + 0.7f)
            drawCircle(Color.White.copy(alpha = a), s.r, Offset(s.x * size.width, s.y * size.height))
        }
    }
}

@Composable
fun ShimmerBox(modifier: Modifier = Modifier) {
    val inf = rememberInfiniteTransition(label = "shimmer")
    val x by inf.animateFloat(
        initialValue = 0f, targetValue = 1200f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Restart),
        label = "shimmerX"
    )
    Box(
        modifier = modifier.background(
            Brush.linearGradient(
                colors = listOf(Color(0xFF1A2A5E), Color(0xFF2A3A7E), Color(0xFF1A2A5E)),
                start = Offset(x - 300f, 0f),
                end   = Offset(x, 0f)
            )
        )
    )
}

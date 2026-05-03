package com.example.cosmoq1

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cosmoq1.ui.components.SpaceBackground
import com.example.cosmoq1.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CosmicExplorerTheme {
                HomeScreen()
            }
        }
    }
}

@Composable
fun HomeScreen() {
    val context = LocalContext.current

    // Entrance animation
    var visible by remember { mutableStateOf(false) }
    val titleScale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.6f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "titleScale"
    )
    val titleAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "titleAlpha"
    )

    LaunchedEffect(Unit) { visible = true }

    SpaceBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Stars decoration
            Text(
                text = "✦ ✧ ✦",
                color = SpaceCyan.copy(alpha = 0.6f),
                fontSize = 18.sp,
                modifier = Modifier.scale(titleScale)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // App title
            Text(
                text = "COSMIC",
                style = TextStyle(
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Black,
                    brush = Brush.horizontalGradient(
                        colors = listOf(SpaceCyan, SpacePurpleLight, SpaceGold)
                    ),
                    shadow = Shadow(
                        color = SpaceCyan.copy(alpha = 0.8f),
                        offset = Offset(0f, 0f),
                        blurRadius = 20f
                    )
                ),
                modifier = Modifier.scale(titleScale),
                textAlign = TextAlign.Center
            )

            Text(
                text = "EXPLORER",
                style = TextStyle(
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    brush = Brush.horizontalGradient(
                        colors = listOf(SpacePurpleLight, SpaceCyan)
                    ),
                    letterSpacing = 8.sp
                ),
                modifier = Modifier.scale(titleScale),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Journey through the cosmos",
                color = StarWhite.copy(alpha = titleAlpha * 0.7f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Explore Universe button
            CosmicButton(
                text = "🚀  Explore Universe",
                gradient = Brush.horizontalGradient(
                    colors = listOf(SpaceAccentBlue, SpaceCyan.copy(alpha = 0.8f))
                ),
                onClick = {
                    context.startActivity(Intent(context, ExploreActivity::class.java))
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Quiz Mode button
            CosmicButton(
                text = "🌟  Quiz Mode",
                gradient = Brush.horizontalGradient(
                    colors = listOf(SpacePurple, SpacePurpleLight.copy(alpha = 0.8f))
                ),
                onClick = {
                    context.startActivity(Intent(context, QuizActivity::class.java))
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "8 planets • 10 quiz questions",
                color = StarWhite.copy(alpha = 0.3f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CosmicButton(
    text: String,
    gradient: Brush,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "buttonScale"
    )

    Button(
        onClick = {
            pressed = true
            onClick()
        },
        modifier = modifier
            .height(58.dp)
            .scale(scale),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = StarWhite
            )
        }
    }

    LaunchedEffect(pressed) {
        if (pressed) {
            kotlinx.coroutines.delay(150)
            pressed = false
        }
    }
}

package com.example.cosmoq1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cosmoq1.data.Planet
import com.example.cosmoq1.data.samplePlanets
import com.example.cosmoq1.ui.components.SpaceGradientBackground
import com.example.cosmoq1.ui.theme.*

class PlanetDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val planetName = intent.getStringExtra("planet_name") ?: ""
        val planet = samplePlanets.find { it.name == planetName }

        setContent {
            CosmicExplorerTheme {
                if (planet != null) {
                    PlanetDetailScreen(planet = planet)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanetDetailScreen(planet: Planet) {
    val context = LocalContext.current
    val planetColor = Color(planet.colorHex)

    var contentVisible by remember { mutableStateOf(false) }
    val orbScale by animateFloatAsState(
        targetValue = if (contentVisible) 1f else 0.3f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "orbScale"
    )

    LaunchedEffect(Unit) { contentVisible = true }

    SpaceGradientBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = planet.name,
                            fontWeight = FontWeight.Bold,
                            color = StarWhite
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { (context as? PlanetDetailActivity)?.finish() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = planetColor
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Planet visual orb
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .scale(orbScale)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    planetColor,
                                    planetColor.copy(alpha = 0.5f),
                                    planetColor.copy(alpha = 0.1f),
                                    Color.Transparent
                                )
                            )
                        )
                        .border(
                            width = 2.dp,
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    planetColor,
                                    planetColor.copy(alpha = 0.2f),
                                    planetColor
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = planetEmoji(planet.name),
                        fontSize = 72.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Glow shadow under orb
                Box(
                    modifier = Modifier
                        .width(160.dp)
                        .height(20.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    planetColor.copy(alpha = 0.4f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                Spacer(modifier = Modifier.height(16.dp))

                AnimatedVisibility(
                    visible = contentVisible,
                    enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { it / 3 }
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Planet name
                        Text(
                            text = planet.name,
                            style = TextStyle(
                                fontSize = 38.sp,
                                fontWeight = FontWeight.Black,
                                brush = Brush.horizontalGradient(
                                    colors = listOf(planetColor, StarWhite)
                                ),
                                shadow = Shadow(
                                    color = planetColor.copy(alpha = 0.6f),
                                    offset = Offset(0f, 0f),
                                    blurRadius = 16f
                                )
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Stats row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatChip(label = "Distance", value = planet.distanceFromSun, color = planetColor)
                            StatChip(label = "Diameter", value = planet.diameter, color = SpaceCyan)
                            StatChip(label = "Moons", value = planet.moons.toString(), color = SpaceGold)
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Description card
                        DetailCard(title = "About ${planet.name}", color = planetColor) {
                            Text(
                                text = planet.fullDescription,
                                color = StarWhite.copy(alpha = 0.85f),
                                fontSize = 15.sp,
                                lineHeight = 24.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Fun facts card
                        DetailCard(title = "✨ Fun Facts", color = SpaceGold) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                planet.funFacts.forEachIndexed { index, fact ->
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text(
                                            text = "${index + 1}",
                                            color = SpaceGold,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier
                                                .background(
                                                    SpaceGold.copy(alpha = 0.15f),
                                                    CircleShape
                                                )
                                                .padding(horizontal = 7.dp, vertical = 2.dp)
                                        )
                                        Text(
                                            text = fact,
                                            color = StarWhite.copy(alpha = 0.8f),
                                            fontSize = 14.sp,
                                            lineHeight = 20.sp,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun StatChip(label: String, value: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(
                color.copy(alpha = 0.1f),
                RoundedCornerShape(12.dp)
            )
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = StarWhite.copy(alpha = 0.5f),
            fontSize = 10.sp,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DetailCard(
    title: String,
    color: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBackground, RoundedCornerShape(16.dp))
                .border(
                    1.dp,
                    Brush.horizontalGradient(
                        listOf(color.copy(alpha = 0.4f), CardBorder.copy(alpha = 0.2f))
                    ),
                    RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = title,
                    color = color,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                content()
            }
        }
    }
}

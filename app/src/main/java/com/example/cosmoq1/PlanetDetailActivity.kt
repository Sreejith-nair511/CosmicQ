package com.example.cosmoq1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cosmoq1.data.Planet
import com.example.cosmoq1.data.samplePlanets
import com.example.cosmoq1.ui.components.GlassCard
import com.example.cosmoq1.ui.components.SpaceGradientBackground
import com.example.cosmoq1.ui.theme.*
import kotlinx.coroutines.delay

class PlanetDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val planetName = intent.getStringExtra("planet_name") ?: ""
        val planet = samplePlanets.find { it.name == planetName }
        setContent {
            CosmicExplorerTheme {
                if (planet != null) PlanetDetailScreen(planet = planet)
            }
        }
    }
}

@Composable
fun TypingText(
    fullText: String,
    color: Color,
    fontSize: androidx.compose.ui.unit.TextUnit,
    lineHeight: androidx.compose.ui.unit.TextUnit,
    modifier: Modifier = Modifier
) {
    var displayedText by remember(fullText) { mutableStateOf("") }
    LaunchedEffect(fullText) {
        displayedText = ""
        fullText.forEachIndexed { index, _ ->
            delay(12L)
            displayedText = fullText.substring(0, index + 1)
        }
    }
    Text(
        text = displayedText,
        color = color,
        fontSize = fontSize,
        lineHeight = lineHeight,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanetDetailScreen(planet: Planet) {
    val context = LocalContext.current
    val planetColor = Color(planet.colorHex)
    val scrollState = rememberScrollState()

    var contentVisible by remember { mutableStateOf(false) }
    val orbScale by animateFloatAsState(
        targetValue = if (contentVisible) 1f else 0.2f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "orbScale"
    )
    val orbAlpha by animateFloatAsState(
        targetValue = if (contentVisible) 1f else 0f,
        animationSpec = tween(600),
        label = "orbAlpha"
    )

    LaunchedEffect(Unit) { contentVisible = true }

    // Parallax: orb moves up as user scrolls
    val parallaxOffset = scrollState.value * 0.4f

    SpaceGradientBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(planet.name, fontWeight = FontWeight.Bold, color = StarWhite)
                    },
                    navigationIcon = {
                        IconButton(onClick = { (context as? PlanetDetailActivity)?.finish() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = planetColor
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Parallax planet orb
                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            translationY = -parallaxOffset
                            alpha = orbAlpha
                            scaleX = orbScale
                            scaleY = orbScale
                        }
                        .size(200.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    planetColor,
                                    planetColor.copy(alpha = 0.6f),
                                    planetColor.copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                        .border(
                            2.dp,
                            Brush.sweepGradient(
                                listOf(planetColor, planetColor.copy(0.2f), planetColor)
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = planetEmoji(planet.name), fontSize = 80.sp)
                }

                // Glow shadow
                Box(
                    modifier = Modifier
                        .width(180.dp)
                        .height(24.dp)
                        .graphicsLayer { translationY = -parallaxOffset * 0.4f }
                        .background(
                            Brush.radialGradient(
                                listOf(planetColor.copy(alpha = 0.5f), Color.Transparent)
                            )
                        )
                )

                Spacer(modifier = Modifier.height(12.dp))

                AnimatedVisibility(
                    visible = contentVisible,
                    enter = fadeIn(tween(700)) + slideInVertically(tween(700)) { it / 3 }
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Planet name
                        Text(
                            text = planet.name,
                            style = TextStyle(
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Black,
                                brush = Brush.horizontalGradient(listOf(planetColor, StarWhite)),
                                shadow = Shadow(
                                    color = planetColor.copy(alpha = 0.7f),
                                    offset = Offset(0f, 0f),
                                    blurRadius = 20f
                                )
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Type badge
                        Text(
                            text = planet.type,
                            color = planetColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .background(planetColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .border(1.dp, planetColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 14.dp, vertical = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Stats grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            DetailStatCard("Distance", planet.distanceFromSun, planetColor, Modifier.weight(1f))
                            DetailStatCard("Diameter", planet.diameter, SpaceCyan, Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            DetailStatCard("Gravity", planet.gravity, SpaceGold, Modifier.weight(1f))
                            DetailStatCard("Temperature", planet.temperature, SpaceOrange, Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            DetailStatCard("Moons", planet.moons.toString(), SpacePurpleLight, Modifier.weight(1f))
                            DetailStatCard("Orbital Period", planet.orbitalPeriod, SpaceGreen, Modifier.weight(1f))
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Description with typing animation
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderColor = planetColor.copy(alpha = 0.4f)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = "About ${planet.name}",
                                    color = planetColor,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                TypingText(
                                    fullText = planet.fullDescription,
                                    color = StarWhite.copy(alpha = 0.85f),
                                    fontSize = 14.sp,
                                    lineHeight = 22.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Fun facts
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderColor = SpaceGold.copy(alpha = 0.4f)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text("Scientific Facts", color = SpaceGold, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(12.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    planet.funFacts.forEachIndexed { index, fact ->
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .background(SpaceGold.copy(alpha = 0.2f), CircleShape)
                                                    .border(1.dp, SpaceGold.copy(alpha = 0.5f), CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("${index + 1}", color = SpaceGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Text(fact, color = StarWhite.copy(alpha = 0.85f), fontSize = 13.sp, lineHeight = 20.sp, modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Composition & Atmosphere
                        if (planet.composition.isNotBlank()) {
                            GlassCard(modifier = Modifier.fillMaxWidth(), borderColor = SpaceCyan.copy(0.35f)) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Text("Composition", color = SpaceCyan, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(planet.composition, color = StarWhite.copy(0.8f), fontSize = 13.sp, lineHeight = 20.sp)
                                    if (planet.atmosphere.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text("Atmosphere", color = SpaceCyan, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(planet.atmosphere, color = StarWhite.copy(0.8f), fontSize = 13.sp, lineHeight = 20.sp)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Magnetic field + Exploration
                        if (planet.magneticField.isNotBlank()) {
                            GlassCard(modifier = Modifier.fillMaxWidth(), borderColor = SpacePurpleLight.copy(0.35f)) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Text("Magnetic Field", color = SpacePurpleLight, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(planet.magneticField, color = StarWhite.copy(0.8f), fontSize = 13.sp, lineHeight = 20.sp)
                                    if (planet.explorationHighlight.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text("Exploration", color = SpacePurpleLight, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(planet.explorationHighlight, color = StarWhite.copy(0.8f), fontSize = 13.sp, lineHeight = 20.sp)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Research note
                        if (planet.researchNote.isNotBlank()) {
                            GlassCard(modifier = Modifier.fillMaxWidth(), borderColor = SpaceOrange.copy(0.35f)) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Text("Open Research Questions", color = SpaceOrange, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(planet.researchNote, color = StarWhite.copy(0.8f), fontSize = 13.sp, lineHeight = 20.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DetailStatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    GlassCard(
        modifier = modifier,
        cornerRadius = 14.dp,
        borderColor = color.copy(alpha = 0.3f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                color = StarWhite.copy(alpha = 0.45f),
                fontSize = 10.sp,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = color,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

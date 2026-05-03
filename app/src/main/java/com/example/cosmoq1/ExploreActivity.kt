package com.example.cosmoq1

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cosmoq1.data.Planet
import com.example.cosmoq1.data.samplePlanets
import com.example.cosmoq1.ui.components.SpaceGradientBackground
import com.example.cosmoq1.ui.theme.*
import kotlinx.coroutines.delay

class ExploreActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CosmicExplorerTheme {
                ExploreScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen() {
    val context = LocalContext.current

    SpaceGradientBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "🌌 Explore Universe",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = StarWhite
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { (context as? ExploreActivity)?.finish() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = SpaceCyan
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp, top = 8.dp)
            ) {
                item {
                    Text(
                        text = "Our Solar System",
                        color = StarWhite.copy(alpha = 0.5f),
                        fontSize = 13.sp,
                        letterSpacing = 2.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                itemsIndexed(samplePlanets) { index, planet ->
                    AnimatedPlanetCard(
                        planet = planet,
                        index = index,
                        onClick = {
                            val intent = Intent(context, PlanetDetailActivity::class.java).apply {
                                putExtra("planet_name", planet.name)
                            }
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedPlanetCard(
    planet: Planet,
    index: Int,
    onClick: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(index * 80L)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(400)) +
                slideInVertically(
                    animationSpec = tween(400),
                    initialOffsetY = { it / 2 }
                )
    ) {
        PlanetCard(planet = planet, onClick = onClick)
    }
}

@Composable
fun PlanetCard(planet: Planet, onClick: () -> Unit) {
    val planetColor = Color(planet.colorHex)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            CardBackground,
                            CardBackground.copy(alpha = 0.8f)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            planetColor.copy(alpha = 0.5f),
                            CardBorder.copy(alpha = 0.3f)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Planet color orb
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    planetColor.copy(alpha = 0.9f),
                                    planetColor.copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            )
                        )
                        .border(
                            width = 1.5.dp,
                            color = planetColor.copy(alpha = 0.6f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = planetEmoji(planet.name),
                        fontSize = 24.sp
                    )
                }

                // Planet info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = planet.name,
                        color = StarWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = planet.shortDescription,
                        color = StarWhite.copy(alpha = 0.65f),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        PlanetStat(label = "Moons", value = planet.moons.toString())
                        PlanetStat(label = "Diameter", value = planet.diameter)
                    }
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "View details",
                    tint = planetColor.copy(alpha = 0.7f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun PlanetStat(label: String, value: String) {
    Column {
        Text(
            text = label,
            color = StarWhite.copy(alpha = 0.4f),
            fontSize = 10.sp,
            letterSpacing = 1.sp
        )
        Text(
            text = value,
            color = SpaceCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

fun planetEmoji(name: String): String = when (name) {
    "Mercury" -> "☿"
    "Venus"   -> "♀"
    "Earth"   -> "🌍"
    "Mars"    -> "♂"
    "Jupiter" -> "♃"
    "Saturn"  -> "♄"
    "Uranus"  -> "⛢"
    "Neptune" -> "♆"
    else      -> "🪐"
}

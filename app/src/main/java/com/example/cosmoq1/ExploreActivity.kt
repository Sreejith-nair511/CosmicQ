package com.example.cosmoq1

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cosmoq1.data.Planet
import com.example.cosmoq1.ui.components.GlassCard
import com.example.cosmoq1.ui.components.SpaceGradientBackground
import com.example.cosmoq1.ui.theme.*
import com.example.cosmoq1.viewmodel.ExploreViewModel
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
fun ExploreScreen(vm: ExploreViewModel = viewModel()) {
    val context = LocalContext.current
    val planets by vm.filteredPlanets.collectAsStateWithLifecycle()
    val searchQuery by vm.searchQuery.collectAsStateWithLifecycle()

    SpaceGradientBackground {
        Scaffold(
            topBar = {
                Column {
                    TopAppBar(
                        title = {
                            Text(
                                "Explore Universe",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = StarWhite
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { (context as? ExploreActivity)?.finish() }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = SpaceCyan
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                    )
                    // Search bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = vm::onSearchQueryChange,
                        placeholder = {
                            Text("Search planets or type...", color = StarWhite.copy(alpha = 0.4f), fontSize = 14.sp)
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = SpaceCyan)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SpaceCyan,
                            unfocusedBorderColor = CardBorder,
                            focusedTextColor = StarWhite,
                            unfocusedTextColor = StarWhite,
                            cursorColor = SpaceCyan,
                            focusedContainerColor = CardBackground.copy(alpha = 0.6f),
                            unfocusedContainerColor = CardBackground.copy(alpha = 0.4f)
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            if (planets.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No planets found", color = StarWhite.copy(alpha = 0.5f), fontSize = 16.sp)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp, top = 8.dp)
                ) {
                    itemsIndexed(planets, key = { _, p -> p.name }) { index, planet ->
                        AnimatedPlanetGridCard(
                            planet = planet,
                            index = index,
                            onClick = {
                                context.startActivity(
                                    Intent(context, PlanetDetailActivity::class.java)
                                        .putExtra("planet_name", planet.name)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedPlanetGridCard(planet: Planet, index: Int, onClick: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(planet.name) {
        delay(index * 60L)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { it }
    ) {
        PlanetGridCard(planet = planet, onClick = onClick)
    }
}

@Composable
fun PlanetGridCard(planet: Planet, onClick: () -> Unit) {
    val planetColor = Color(planet.colorHex)
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.93f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy),
        label = "cardScale"
    )

    LaunchedEffect(pressed) {
        if (pressed) { delay(150); pressed = false }
    }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.78f)
            .scale(scale)
            .clickable { pressed = true; onClick() },
        cornerRadius = 20.dp,
        borderColor = planetColor.copy(alpha = 0.4f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Planet orb
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                planetColor.copy(alpha = 0.95f),
                                planetColor.copy(alpha = 0.4f),
                                Color.Transparent
                            )
                        )
                    )
                    .border(1.5.dp, planetColor.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = planetEmoji(planet.name), fontSize = 36.sp)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = planet.name,
                    color = StarWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = planet.type,
                    color = planetColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .background(planetColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = planet.shortDescription,
                    color = StarWhite.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 15.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MiniStat(label = "Moons", value = planet.moons.toString(), color = SpaceGold)
                MiniStat(label = "Gravity", value = planet.gravity, color = SpaceCyan)
            }
        }
    }
}

@Composable
fun MiniStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = StarWhite.copy(alpha = 0.4f), fontSize = 9.sp, letterSpacing = 0.5.sp)
        Text(text = value, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
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

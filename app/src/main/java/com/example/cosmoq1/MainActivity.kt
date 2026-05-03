package com.example.cosmoq1

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cosmoq1.ui.components.AnimatedStarField
import com.example.cosmoq1.ui.theme.*
import com.example.cosmoq1.viewmodel.ThemeViewModel
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeVm: ThemeViewModel = viewModel()
            val currentTheme by themeVm.currentTheme.collectAsStateWithLifecycle()
            CosmicSwipeTheme(appTheme = currentTheme) {
                HomeScreen(themeVm = themeVm)
            }
        }
    }
}

@Composable
fun HomeScreen(themeVm: ThemeViewModel) {
    val context = LocalContext.current
    val currentTheme by themeVm.currentTheme.collectAsStateWithLifecycle()
    var showThemePicker by remember { mutableStateOf(false) }

    var visible by remember { mutableStateOf(false) }
    val titleAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(900),
        label = "titleAlpha"
    )
    val titleOffsetY by animateFloatAsState(
        targetValue = if (visible) 0f else 60f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "titleY"
    )
    val buttonsAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(900, delayMillis = 350),
        label = "btnsAlpha"
    )

    LaunchedEffect(Unit) { delay(80); visible = true }

    val bgColor = MaterialTheme.colorScheme.background

    Box(modifier = Modifier.fillMaxSize().background(bgColor)) {
        AnimatedStarField(bgColor = bgColor)

        // Nebula glows
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(MaterialTheme.colorScheme.secondary.copy(0.12f), Color.Transparent),
                    center = Offset(size.width * 0.15f, size.height * 0.25f),
                    radius = size.width * 0.55f
                ),
                radius = size.width * 0.55f,
                center = Offset(size.width * 0.15f, size.height * 0.25f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(MaterialTheme.colorScheme.primary.copy(0.08f), Color.Transparent),
                    center = Offset(size.width * 0.85f, size.height * 0.75f),
                    radius = size.width * 0.45f
                ),
                radius = size.width * 0.45f,
                center = Offset(size.width * 0.85f, size.height * 0.75f)
            )
        }

        // Theme picker button
        IconButton(
            onClick = { showThemePicker = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .systemBarsPadding()
                .padding(12.dp)
        ) {
            Icon(Icons.Default.Palette, contentDescription = "Theme", tint = MaterialTheme.colorScheme.primary)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Title
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .alpha(titleAlpha)
                    .offset(y = titleOffsetY.dp)
            ) {
                Text(
                    text = "COSMIC",
                    style = TextStyle(
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Black,
                        brush = Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        ),
                        shadow = Shadow(
                            color = MaterialTheme.colorScheme.primary.copy(0.8f),
                            offset = Offset(0f, 0f),
                            blurRadius = 28f
                        ),
                        letterSpacing = 6.sp
                    ),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "EXPLORER",
                    style = TextStyle(
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        brush = Brush.horizontalGradient(
                            listOf(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.primary)
                        ),
                        letterSpacing = 10.sp
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Journey through the cosmos",
                    color = StarWhite.copy(0.5f),
                    fontSize = 13.sp,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(52.dp))

            Column(
                modifier = Modifier.fillMaxWidth().alpha(buttonsAlpha),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                CosmicButton(
                    text = "Cosmic Swipe",
                    gradient = Brush.horizontalGradient(
                        listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.primary.copy(0.9f))
                    ),
                    icon = "",
                    onClick = { context.startActivity(Intent(context, SwipeActivity::class.java)) },
                    modifier = Modifier.fillMaxWidth()
                )
                CosmicButton(
                    text = "Explore Universe",
                    gradient = Brush.horizontalGradient(listOf(Color(0xFF1565C0), SpaceCyan.copy(0.9f))),
                    icon = "",
                    onClick = { context.startActivity(Intent(context, ExploreActivity::class.java)) },
                    modifier = Modifier.fillMaxWidth()
                )
                CosmicButton(
                    text = "Space Quiz",
                    gradient = Brush.horizontalGradient(listOf(SpacePurple, Color(0xFF9C27B0))),
                    icon = "",
                    onClick = { context.startActivity(Intent(context, QuizActivity::class.java)) },
                    modifier = Modifier.fillMaxWidth()
                )
                CosmicButton(
                    text = "Daily Space",
                    gradient = Brush.horizontalGradient(listOf(Color(0xFF1B5E20), Color(0xFF00BCD4))),
                    icon = "",
                    onClick = { context.startActivity(Intent(context, DailySpaceActivity::class.java)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text(
                "8 planets  |  Quiz  |  NASA APOD  |  Live News",
                color = StarWhite.copy(0.22f),
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(buttonsAlpha)
            )
        }
    }

    if (showThemePicker) {
        ThemePickerDialog(
            currentTheme = currentTheme,
            onSelect = { themeVm.setTheme(it); showThemePicker = false },
            onDismiss = { showThemePicker = false }
        )
    }
}

@Composable
fun ThemePickerDialog(
    currentTheme: AppTheme,
    onSelect: (AppTheme) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF0F1F4A))
                .border(1.dp, Color.White.copy(0.15f), RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column {
                Text("Select Theme", color = StarWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(20.dp))

                ThemeOption(
                    label = "Deep Space",
                    subtitle = "Black + Neon Blue",
                    color1 = DS_Background, color2 = DS_Primary,
                    selected = currentTheme == AppTheme.DEEP_SPACE,
                    onClick = { onSelect(AppTheme.DEEP_SPACE) }
                )
                Spacer(modifier = Modifier.height(12.dp))
                ThemeOption(
                    label = "Galaxy Purple",
                    subtitle = "Dark Purple + Violet",
                    color1 = GP_Background, color2 = GP_Primary,
                    selected = currentTheme == AppTheme.GALAXY_PURPLE,
                    onClick = { onSelect(AppTheme.GALAXY_PURPLE) }
                )
                Spacer(modifier = Modifier.height(12.dp))
                ThemeOption(
                    label = "Solar Gold",
                    subtitle = "Dark + Gold + Orange",
                    color1 = SG_Background, color2 = SG_Primary,
                    selected = currentTheme == AppTheme.SOLAR_GOLD,
                    onClick = { onSelect(AppTheme.SOLAR_GOLD) }
                )
            }
        }
    }
}

@Composable
fun ThemeOption(
    label: String,
    subtitle: String,
    color1: Color,
    color2: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) color2.copy(0.15f) else Color.White.copy(0.05f))
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) color2 else Color.White.copy(0.1f),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Color preview swatch
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Brush.horizontalGradient(listOf(color1, color2)))
                .border(1.dp, Color.White.copy(0.2f), CircleShape)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = StarWhite, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = StarWhite.copy(0.5f), fontSize = 12.sp)
        }
        if (selected) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(color2),
                contentAlignment = Alignment.Center
            ) {
                Text("", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
fun CosmicButton(
    text: String,
    gradient: Brush,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: String = ""
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.93f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
        label = "btnScale"
    )
    LaunchedEffect(pressed) { if (pressed) { delay(150); pressed = false } }

    Button(
        onClick = { pressed = true; onClick() },
        modifier = modifier
            .height(60.dp)
            .graphicsLayerScale(scale),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient, RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = StarWhite,
                letterSpacing = 0.5.sp
            )
        }
    }
}

// Helper to avoid importing graphicsLayer in the button
private fun Modifier.graphicsLayerScale(scale: Float): Modifier =
    this.then(androidx.compose.ui.draw.scale(scale))

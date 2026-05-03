package com.example.cosmoq1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.cosmoq1.data.ApodResponse
import com.example.cosmoq1.ui.components.GlassCard
import com.example.cosmoq1.ui.components.ShimmerBox
import com.example.cosmoq1.ui.components.SpaceGradientBackground
import com.example.cosmoq1.ui.theme.*
import com.example.cosmoq1.viewmodel.ApodUiState
import com.example.cosmoq1.viewmodel.DailySpaceViewModel

class DailySpaceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CosmicExplorerTheme {
                DailySpaceScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailySpaceScreen(vm: DailySpaceViewModel = viewModel()) {
    val context = LocalContext.current
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    SpaceGradientBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Daily Space", fontWeight = FontWeight.Bold, color = StarWhite) },
                    navigationIcon = {
                        IconButton(onClick = { (context as? DailySpaceActivity)?.finish() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SpaceCyan)
                        }
                    },
                    actions = {
                        IconButton(onClick = vm::forceRefresh) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = SpaceCyan)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (val state = uiState) {
                    is ApodUiState.Loading -> ApodShimmer()
                    is ApodUiState.Success -> ApodContent(apod = state.data)
                    is ApodUiState.Error   -> ApodError(message = state.message, onRetry = vm::fetchApod)
                }
            }
        }
    }
}

@Composable
fun ApodShimmer() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Image shimmer
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(20.dp))
        )
        // Title shimmer
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(24.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        // Date shimmer
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .height(16.dp)
                .clip(RoundedCornerShape(6.dp))
        )
        // Description shimmer lines
        repeat(6) {
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth(if (it == 5) 0.6f else 1f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(6.dp))
            )
        }
    }
}

@Composable
fun ApodContent(apod: ApodResponse) {
    var contentVisible by remember { mutableStateOf(false) }
    LaunchedEffect(apod) { contentVisible = true }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AnimatedVisibility(
            visible = contentVisible,
            enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { -it / 3 }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Image
                if (apod.mediaType == "image") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .clip(RoundedCornerShape(20.dp))
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(apod.url)
                                .crossfade(true)
                                .build(),
                            contentDescription = apod.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        // Gradient overlay at bottom
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .align(Alignment.BottomCenter)
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color.Transparent, Color(0xFF050510))
                                    )
                                )
                        )
                    }
                } else {
                    // Video or unsupported type placeholder
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        cornerRadius = 20.dp
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🎬", fontSize = 48.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Video content available",
                                    color = StarWhite.copy(0.6f),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                // Title
                Text(
                    text = apod.title,
                    color = StarWhite,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 30.sp
                )

                // Date + copyright row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = apod.date,
                        color = SpaceCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .background(SpaceCyan.copy(0.1f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                    apod.copyright?.let { credit ->
                        Text(
                            text = "© $credit",
                            color = StarWhite.copy(0.4f),
                            fontSize = 11.sp
                        )
                    }
                }

                // Description card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = SpaceCyan.copy(0.3f)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "About This Image",
                            color = SpaceCyan,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = apod.explanation,
                            color = StarWhite.copy(0.8f),
                            fontSize = 14.sp,
                            lineHeight = 22.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ApodError(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🌌", fontSize = 64.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Could not load NASA picture",
            color = StarWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            message,
            color = StarWhite.copy(0.5f),
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        CosmicButton(
            text = "Try Again",
            gradient = Brush.horizontalGradient(listOf(SpaceAccentBlue, SpaceCyan.copy(0.8f))),
            onClick = onRetry,
            icon = "🔄",
            modifier = Modifier.fillMaxWidth()
        )
    }
}

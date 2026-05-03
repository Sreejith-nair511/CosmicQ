package com.example.cosmoq1

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cosmoq1.ui.components.AnimatedStarField
import com.example.cosmoq1.ui.components.ShimmerBox
import com.example.cosmoq1.ui.swipe.SwipeCardStack
import com.example.cosmoq1.ui.theme.*
import com.example.cosmoq1.util.TtsManager
import com.example.cosmoq1.viewmodel.SwipeUiState
import com.example.cosmoq1.viewmodel.SwipeViewModel

class SwipeActivity : ComponentActivity() {

    private lateinit var ttsManager: TtsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ttsManager = TtsManager(this)
        setContent {
            CosmicExplorerTheme {
                SwipeScreen(ttsManager = ttsManager)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ttsManager.shutdown()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeScreen(
    vm: SwipeViewModel = viewModel(),
    ttsManager: TtsManager
) {
    val context = LocalContext.current
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val toast   by vm.toastMessage.collectAsStateWithLifecycle()

    // Show toast snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(toast) {
        toast?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            vm.clearToast()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedStarField(bgColor = DS_Background)

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Cosmic Swipe",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = StarWhite
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { (context as? SwipeActivity)?.finish() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SpaceCyan)
                        }
                    },
                    actions = {
                        IconButton(onClick = { context.startActivity(Intent(context, SavedActivity::class.java)) }) {
                            Icon(Icons.Default.Bookmark, contentDescription = "Saved", tint = SpaceGold)
                        }
                        IconButton(onClick = vm::loadCards) {
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
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                when (val state = uiState) {
                    is SwipeUiState.Loading -> SwipeLoadingState()
                    is SwipeUiState.Error   -> SwipeErrorState(message = state.message, onRetry = vm::loadCards)
                    is SwipeUiState.Success -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Hint row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Swipe left to skip",
                                    color = SpaceRed.copy(0.7f),
                                    fontSize = 12.sp
                                )
                                Text(
                                    "Swipe right to save",
                                    color = SpaceGreen.copy(0.7f),
                                    fontSize = 12.sp
                                )
                            }

                            SwipeCardStack(
                                cards = state.cards,
                                onSwipeRight = { card ->
                                    vm.onSwipeRight(card)
                                    ttsManager.speak("${card.title}. ${card.summary.take(100)}")
                                },
                                onSwipeLeft = { card ->
                                    vm.onSwipeLeft(card)
                                    ttsManager.stop()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp)
                            )

                            // Action buttons
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 48.dp, vertical = 16.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ActionButton(
                                    label = "Skip",
                                    color = SpaceRed,
                                    icon = "X"
                                )
                                ActionButton(
                                    label = "Save",
                                    color = SpaceGreen,
                                    icon = "+"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActionButton(label: String, color: Color, icon: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f))
                .then(
                    Modifier.background(
                        Brush.radialGradient(listOf(color.copy(0.2f), Color.Transparent)),
                        CircleShape
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, color = color, fontSize = 22.sp, fontWeight = FontWeight.Black)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = color.copy(0.7f), fontSize = 11.sp)
    }
}

@Composable
fun SwipeLoadingState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(480.dp)
                .clip(RoundedCornerShape(24.dp))
        )
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(20.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(14.dp)
                .clip(RoundedCornerShape(6.dp))
        )
    }
}

@Composable
fun SwipeErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Connection Error", color = StarWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(message, color = StarWhite.copy(0.5f), fontSize = 13.sp, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(28.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = SpaceCyan),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Retry", color = SpaceBlack, fontWeight = FontWeight.Bold)
        }
    }
}

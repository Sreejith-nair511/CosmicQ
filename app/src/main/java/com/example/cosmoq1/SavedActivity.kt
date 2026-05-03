package com.example.cosmoq1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.cosmoq1.data.SwipeCard
import com.example.cosmoq1.ui.components.AnimatedStarField
import com.example.cosmoq1.ui.components.CategoryBadge
import com.example.cosmoq1.ui.theme.*
import com.example.cosmoq1.viewmodel.SwipeViewModel

class SavedActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CosmicExplorerTheme {
                SavedScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedScreen(vm: SwipeViewModel = viewModel()) {
    val context = LocalContext.current
    val saved by vm.savedCards.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedStarField(bgColor = DS_Background)

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Saved Articles",
                            fontWeight = FontWeight.Bold,
                            color = StarWhite
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { (context as? SavedActivity)?.finish() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SpaceCyan)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            if (saved.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No saved articles yet.", color = StarWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Swipe right on cards to save them.", color = StarWhite.copy(0.5f), fontSize = 14.sp)
                    }
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
                    itemsIndexed(saved, key = { _, c -> c.id }) { index, card ->
                        AnimatedSavedCard(card = card, index = index, onDelete = { vm.removeFromSaved(card.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedSavedCard(card: SwipeCard, index: Int, onDelete: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(card.id) {
        kotlinx.coroutines.delay(index * 50L)
        visible = true
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(350)) + slideInVertically(tween(350)) { it }
    ) {
        SavedCardItem(card = card, onDelete = onDelete)
    }
}

@Composable
fun SavedCardItem(card: SwipeCard, onDelete: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.72f)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Color.White.copy(0.1f), RoundedCornerShape(16.dp))
    ) {
        // Image
        if (card.imageUrl.isNotBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(card.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = card.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(listOf(Color(0xFF0A1530), Color(0xFF000010))))
            )
        }

        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(listOf(Color.Transparent, Color(0xEE000010)))
                )
        )

        // Delete button
        IconButton(
            onClick = onDelete,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(32.dp)
                .background(Color.Black.copy(0.5f), RoundedCornerShape(8.dp))
        ) {
            Icon(Icons.Default.Delete, contentDescription = "Remove", tint = SpaceRed, modifier = Modifier.size(16.dp))
        }

        // Text content
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(10.dp)
        ) {
            CategoryBadge(category = card.category)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = card.title,
                color = StarWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 17.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = card.source,
                color = StarWhite.copy(0.5f),
                fontSize = 10.sp
            )
        }
    }
}

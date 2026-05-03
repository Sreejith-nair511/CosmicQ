package com.example.cosmoq1.ui.swipe

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.cosmoq1.data.SwipeCard
import com.example.cosmoq1.ui.components.CategoryBadge
import com.example.cosmoq1.ui.theme.StarWhite
import kotlinx.coroutines.launch
import kotlin.math.abs

// Swipe threshold in pixels
private const val SWIPE_THRESHOLD = 300f

enum class SwipeDirection { LEFT, RIGHT, NONE }

@Composable
fun SwipeCardStack(
    cards: List<SwipeCard>,
    onSwipeRight: (SwipeCard) -> Unit,
    onSwipeLeft: (SwipeCard) -> Unit,
    modifier: Modifier = Modifier
) {
    var topIndex by remember { mutableIntStateOf(0) }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // Render up to 3 cards in reverse so top card is drawn last
        val visibleCount = minOf(3, cards.size - topIndex)
        if (visibleCount <= 0) {
            EmptyDeckMessage()
            return@Box
        }

        for (i in (visibleCount - 1) downTo 0) {
            val cardIndex = topIndex + i
            val isTop = i == 0

            if (isTop) {
                DraggableCard(
                    card = cards[cardIndex],
                    stackPosition = i,
                    onSwiped = { direction ->
                        if (direction == SwipeDirection.RIGHT) onSwipeRight(cards[cardIndex])
                        else onSwipeLeft(cards[cardIndex])
                        topIndex++
                    }
                )
            } else {
                // Background cards — scale + translate to create depth
                val scale = 1f - (i * 0.05f)
                val offsetY = (i * 16).dp
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .fillMaxHeight(0.72f)
                        .offset(y = offsetY)
                        .graphicsLayer { scaleX = scale; scaleY = scale }
                        .shadow(4.dp, RoundedCornerShape(24.dp))
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF0A1530))
                )
            }
        }
    }
}

@Composable
fun DraggableCard(
    card: SwipeCard,
    stackPosition: Int,
    onSwiped: (SwipeDirection) -> Unit
) {
    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }

    val rotation by animateFloatAsState(
        targetValue = (offsetX.value / 30f).coerceIn(-15f, 15f),
        animationSpec = spring(Spring.DampingRatioMediumBouncy),
        label = "rotation"
    )
    val alpha by animateFloatAsState(
        targetValue = (1f - abs(offsetX.value) / (SWIPE_THRESHOLD * 1.5f)).coerceIn(0f, 1f),
        label = "cardAlpha"
    )

    // Overlay tint
    val rightTint = (offsetX.value / SWIPE_THRESHOLD).coerceIn(0f, 1f)
    val leftTint  = (-offsetX.value / SWIPE_THRESHOLD).coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxWidth(0.88f)
            .fillMaxHeight(0.72f)
            .graphicsLayer {
                translationX = offsetX.value
                translationY = offsetY.value
                rotationZ    = rotation
                this.alpha   = alpha
            }
            .shadow(16.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .pointerInput(card.id) {
                detectDragGestures(
                    onDragEnd = {
                        scope.launch {
                            when {
                                offsetX.value > SWIPE_THRESHOLD -> {
                                    offsetX.animateTo(2000f, tween(300))
                                    onSwiped(SwipeDirection.RIGHT)
                                    offsetX.snapTo(0f); offsetY.snapTo(0f)
                                }
                                offsetX.value < -SWIPE_THRESHOLD -> {
                                    offsetX.animateTo(-2000f, tween(300))
                                    onSwiped(SwipeDirection.LEFT)
                                    offsetX.snapTo(0f); offsetY.snapTo(0f)
                                }
                                else -> {
                                    offsetX.animateTo(0f, spring(Spring.DampingRatioMediumBouncy))
                                    offsetY.animateTo(0f, spring(Spring.DampingRatioMediumBouncy))
                                }
                            }
                        }
                    },
                    onDrag = { change, drag ->
                        change.consume()
                        scope.launch {
                            offsetX.snapTo(offsetX.value + drag.x)
                            offsetY.snapTo(offsetY.value + drag.y * 0.3f)
                        }
                    }
                )
            }
    ) {
        CardContent(card = card)

        // Right swipe overlay (green tint)
        if (rightTint > 0.05f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF22C55E).copy(alpha = rightTint * 0.45f))
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(24.dp)
            ) {
                Text(
                    "SAVE",
                    color = Color(0xFF22C55E),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier
                        .background(Color.Black.copy(0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        // Left swipe overlay (red tint)
        if (leftTint > 0.05f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFEF4444).copy(alpha = leftTint * 0.45f))
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(24.dp)
            ) {
                Text(
                    "SKIP",
                    color = Color(0xFFEF4444),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier
                        .background(Color.Black.copy(0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun CardContent(card: SwipeCard) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
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
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF0A1530), Color(0xFF000010))
                        )
                    )
            )
        }

        // Gradient overlay — bottom 60%
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.65f)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color(0xCC000010), Color(0xF0000010))
                    )
                )
        )

        // Content
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp)
        ) {
            CategoryBadge(category = card.category)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = card.title,
                color = StarWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 28.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = card.summary,
                color = StarWhite.copy(alpha = 0.75f),
                fontSize = 13.sp,
                lineHeight = 19.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = card.source,
                    color = StarWhite.copy(alpha = 0.5f),
                    fontSize = 11.sp
                )
                Text(
                    text = card.publishedAt,
                    color = StarWhite.copy(alpha = 0.4f),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun EmptyDeckMessage() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text("All caught up!", color = StarWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Pull to refresh for more content.", color = StarWhite.copy(0.5f), fontSize = 14.sp)
    }
}

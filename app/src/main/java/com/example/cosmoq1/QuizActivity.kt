package com.example.cosmoq1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cosmoq1.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

class QuizActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CosmicExplorerTheme {
                SpaceShooterScreen()
            }
        }
    }
}

// ---- Game data classes ----

data class Bullet(val id: Int, var x: Float, var y: Float)
data class Asteroid(val id: Int, var x: Float, var y: Float, val size: Float, val speed: Float, val angle: Float)
data class Particle(val id: Int, var x: Float, var y: Float, var vx: Float, var vy: Float, var life: Float, val color: Color)
data class Star(val x: Float, val y: Float, val r: Float, val alpha: Float)

enum class GameState { IDLE, PLAYING, GAME_OVER }

// ---- Main composable ----

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpaceShooterScreen() {
    val context = LocalContext.current

    var gameState by remember { mutableStateOf(GameState.IDLE) }
    var score by remember { mutableIntStateOf(0) }
    var lives by remember { mutableIntStateOf(3) }
    var highScore by remember { mutableIntStateOf(0) }

    // Ship position (0..1 normalized x)
    var shipX by remember { mutableFloatStateOf(0.5f) }

    // Game objects
    val bullets    = remember { mutableStateListOf<Bullet>() }
    val asteroids  = remember { mutableStateListOf<Asteroid>() }
    val particles  = remember { mutableStateListOf<Particle>() }
    val stars      = remember { List(80) { Star(Random.nextFloat(), Random.nextFloat(), Random.nextFloat() * 2f + 0.5f, Random.nextFloat() * 0.7f + 0.3f) } }

    var bulletIdCounter  by remember { mutableIntStateOf(0) }
    var asteroidIdCounter by remember { mutableIntStateOf(0) }
    var particleIdCounter by remember { mutableIntStateOf(0) }

    // Auto-fire timer
    var autoFireTick by remember { mutableIntStateOf(0) }

    // Game loop — 60fps tick
    LaunchedEffect(gameState) {
        if (gameState != GameState.PLAYING) return@LaunchedEffect

        while (gameState == GameState.PLAYING) {
            delay(16L) // ~60fps

            // Auto-fire every 20 ticks
            autoFireTick++
            if (autoFireTick >= 20) {
                autoFireTick = 0
                bullets.add(Bullet(bulletIdCounter++, shipX, 0.88f))
            }

            // Move bullets up
            val bulletsToRemove = mutableListOf<Bullet>()
            bullets.forEach { b ->
                b.y -= 0.025f
                if (b.y < 0f) bulletsToRemove.add(b)
            }
            bullets.removeAll(bulletsToRemove)

            // Spawn asteroids
            if (Random.nextFloat() < 0.025f + score * 0.0002f) {
                asteroids.add(
                    Asteroid(
                        id    = asteroidIdCounter++,
                        x     = Random.nextFloat(),
                        y     = -0.05f,
                        size  = Random.nextFloat() * 0.04f + 0.025f,
                        speed = Random.nextFloat() * 0.006f + 0.004f,
                        angle = Random.nextFloat() * 360f
                    )
                )
            }

            // Move asteroids down
            val asteroidsToRemove = mutableListOf<Asteroid>()
            asteroids.forEach { a ->
                a.y += a.speed
                if (a.y > 1.1f) asteroidsToRemove.add(a)
            }
            asteroids.removeAll(asteroidsToRemove)

            // Collision: bullet vs asteroid
            val hitBullets    = mutableSetOf<Int>()
            val hitAsteroids  = mutableSetOf<Int>()
            bullets.forEach { b ->
                asteroids.forEach { a ->
                    val dx = b.x - a.x
                    val dy = b.y - a.y
                    if (sqrt(dx * dx + dy * dy) < a.size + 0.02f) {
                        hitBullets.add(b.id)
                        hitAsteroids.add(a.id)
                        score++
                        // Spawn explosion particles
                        repeat(8) {
                            val angle = Random.nextFloat() * 360f
                            val speed = Random.nextFloat() * 0.012f + 0.004f
                            particles.add(
                                Particle(
                                    id    = particleIdCounter++,
                                    x     = a.x,
                                    y     = a.y,
                                    vx    = cos(Math.toRadians(angle.toDouble())).toFloat() * speed,
                                    vy    = sin(Math.toRadians(angle.toDouble())).toFloat() * speed,
                                    life  = 1f,
                                    color = listOf(SpaceOrange, SpaceGold, SpaceRed, Color.White).random()
                                )
                            )
                        }
                    }
                }
            }
            bullets.removeAll { it.id in hitBullets }
            asteroids.removeAll { it.id in hitAsteroids }

            // Collision: asteroid vs ship
            val shipY = 0.88f
            val shipHitRadius = 0.04f
            val shipHitAsteroids = mutableSetOf<Int>()
            asteroids.forEach { a ->
                val dx = a.x - shipX
                val dy = a.y - shipY
                if (sqrt(dx * dx + dy * dy) < a.size + shipHitRadius) {
                    shipHitAsteroids.add(a.id)
                    lives--
                    if (lives <= 0) {
                        gameState = GameState.GAME_OVER
                        if (score > highScore) highScore = score
                    }
                }
            }
            asteroids.removeAll { it.id in shipHitAsteroids }

            // Update particles
            val deadParticles = mutableListOf<Particle>()
            particles.forEach { p ->
                p.x    += p.vx
                p.y    += p.vy
                p.life -= 0.04f
                if (p.life <= 0f) deadParticles.add(p)
            }
            particles.removeAll(deadParticles)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000010))
    ) {
        // Game canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(gameState) {
                    if (gameState == GameState.PLAYING) {
                        detectDragGestures { _, drag ->
                            shipX = (shipX + drag.x / size.width).coerceIn(0.05f, 0.95f)
                        }
                    }
                }
        ) {
            val w = size.width
            val h = size.height

            // Stars
            stars.forEach { s ->
                drawCircle(Color.White.copy(alpha = s.alpha), s.r, Offset(s.x * w, s.y * h))
            }

            if (gameState == GameState.PLAYING || gameState == GameState.GAME_OVER) {
                // Bullets
                bullets.forEach { b ->
                    drawRect(
                        color  = SpaceCyan,
                        topLeft = Offset(b.x * w - 3f, b.y * h - 12f),
                        size   = Size(6f, 18f)
                    )
                    // Bullet glow
                    drawCircle(SpaceCyan.copy(0.3f), 10f, Offset(b.x * w, b.y * h))
                }

                // Asteroids
                asteroids.forEach { a ->
                    drawAsteroid(a, w, h)
                }

                // Particles
                particles.forEach { p ->
                    drawCircle(p.color.copy(alpha = p.life), 5f * p.life, Offset(p.x * w, p.y * h))
                }

                // Ship
                drawShip(shipX * w, 0.88f * h)
            }
        }

        // HUD overlay
        when (gameState) {
            GameState.IDLE -> IdleOverlay(
                highScore = highScore,
                onStart = {
                    score = 0; lives = 3
                    bullets.clear(); asteroids.clear(); particles.clear()
                    shipX = 0.5f; autoFireTick = 0
                    gameState = GameState.PLAYING
                }
            )
            GameState.PLAYING -> PlayingHud(
                score = score,
                lives = lives,
                onBack = { (context as? QuizActivity)?.finish() }
            )
            GameState.GAME_OVER -> GameOverOverlay(
                score = score,
                highScore = highScore,
                onRestart = {
                    score = 0; lives = 3
                    bullets.clear(); asteroids.clear(); particles.clear()
                    shipX = 0.5f; autoFireTick = 0
                    gameState = GameState.PLAYING
                },
                onBack = { (context as? QuizActivity)?.finish() }
            )
        }
    }
}

// ---- Draw helpers ----

fun DrawScope.drawShip(cx: Float, cy: Float) {
    // Engine glow
    drawCircle(SpaceCyan.copy(0.25f), 28f, Offset(cx, cy + 14f))
    // Body
    val path = androidx.compose.ui.graphics.Path().apply {
        moveTo(cx, cy - 28f)
        lineTo(cx - 18f, cy + 20f)
        lineTo(cx, cy + 10f)
        lineTo(cx + 18f, cy + 20f)
        close()
    }
    drawPath(path, Brush.verticalGradient(listOf(SpaceCyan, SpacePurpleLight), cy - 28f, cy + 20f))
    // Cockpit
    drawCircle(Color.White.copy(0.9f), 5f, Offset(cx, cy - 10f))
    // Left wing
    val leftWing = androidx.compose.ui.graphics.Path().apply {
        moveTo(cx - 18f, cy + 20f)
        lineTo(cx - 32f, cy + 28f)
        lineTo(cx - 10f, cy + 8f)
        close()
    }
    drawPath(leftWing, SpacePurple.copy(0.8f))
    // Right wing
    val rightWing = androidx.compose.ui.graphics.Path().apply {
        moveTo(cx + 18f, cy + 20f)
        lineTo(cx + 32f, cy + 28f)
        lineTo(cx + 10f, cy + 8f)
        close()
    }
    drawPath(rightWing, SpacePurple.copy(0.8f))
    // Thruster flame
    drawCircle(SpaceOrange.copy(0.7f), 8f, Offset(cx, cy + 22f))
    drawCircle(SpaceGold.copy(0.5f), 4f, Offset(cx, cy + 26f))
}

fun DrawScope.drawAsteroid(a: Asteroid, w: Float, h: Float) {
    val cx = a.x * w
    val cy = a.y * h
    val r  = a.size * w
    rotate(a.angle, Offset(cx, cy)) {
        val path = androidx.compose.ui.graphics.Path().apply {
            val pts = 7
            for (i in 0 until pts) {
                val angle = (i.toFloat() / pts) * 360f
                val rad   = r * (0.7f + Random.nextFloat() * 0.3f)
                val px    = cx + cos(Math.toRadians(angle.toDouble())).toFloat() * rad
                val py    = cy + sin(Math.toRadians(angle.toDouble())).toFloat() * rad
                if (i == 0) moveTo(px, py) else lineTo(px, py)
            }
            close()
        }
        drawPath(path, Brush.radialGradient(listOf(Color(0xFF8B7355), Color(0xFF4A3728)), Offset(cx, cy), r))
        drawPath(path, Color(0xFF6B5A45).copy(0.5f), style = androidx.compose.ui.graphics.drawscope.Stroke(2f))
    }
}

// ---- UI overlays ----

@Composable
fun IdleOverlay(highScore: Int, onStart: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                "SPACE SHOOTER",
                color = SpaceCyan,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center
            )
            Text(
                "Drag to move your ship\nDestroy asteroids to score",
                color = StarWhite.copy(0.65f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
            if (highScore > 0) {
                Text(
                    "Best: $highScore",
                    color = SpaceGold,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onStart,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(0.6f).height(52.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(listOf(SpaceAccentBlue, SpaceCyan)),
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("LAUNCH", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 16.sp, letterSpacing = 2.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayingHud(score: Int, lives: Int, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Score: $score", color = SpaceCyan, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        buildString { repeat(lives) { append("* ") } }.trim(),
                        color = SpaceRed,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SpaceCyan)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black.copy(0.5f))
        )
    }
}

@Composable
fun GameOverOverlay(score: Int, highScore: Int, onRestart: () -> Unit, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(0.75f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text("GAME OVER", color = SpaceRed, fontSize = 36.sp, fontWeight = FontWeight.Black, letterSpacing = 4.sp)
            Text("Score: $score", color = StarWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            if (score >= highScore) {
                Text("New High Score!", color = SpaceGold, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            } else {
                Text("Best: $highScore", color = SpaceGold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onRestart,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(0.65f).height(50.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.horizontalGradient(listOf(SpacePurple, SpaceCyan)), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("PLAY AGAIN", color = Color.White, fontWeight = FontWeight.Black, fontSize = 15.sp)
                }
            }
            TextButton(onClick = onBack) {
                Text("Back to Menu", color = StarWhite.copy(0.6f), fontSize = 13.sp)
            }
        }
    }
}

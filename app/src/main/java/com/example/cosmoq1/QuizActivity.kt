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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cosmoq1.data.QuizQuestion
import com.example.cosmoq1.ui.components.GlassCard
import com.example.cosmoq1.ui.components.SpaceGradientBackground
import com.example.cosmoq1.ui.theme.*
import com.example.cosmoq1.viewmodel.QuizViewModel
import kotlinx.coroutines.delay

class QuizActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CosmicExplorerTheme {
                QuizScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(vm: QuizViewModel = viewModel()) {
    val context = LocalContext.current
    val state by vm.uiState.collectAsStateWithLifecycle()

    SpaceGradientBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Space Quiz", fontWeight = FontWeight.Bold, color = StarWhite) },
                    navigationIcon = {
                        IconButton(onClick = { (context as? QuizActivity)?.finish() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SpacePurpleLight)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            if (state.questions.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = SpaceCyan)
                }
                return@Scaffold
            }

            if (state.isFinished) {
                QuizResultScreen(
                    score = state.score,
                    total = state.questions.size,
                    onRestart = vm::restartQuiz,
                    modifier = Modifier.padding(innerPadding)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    QuizProgressBar(
                        current = state.currentIndex,
                        total = state.questions.size,
                        score = state.score
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    AnimatedContent(
                        targetState = state.currentIndex,
                        transitionSpec = {
                            (slideInHorizontally(tween(350)) { it } + fadeIn(tween(350)))
                                .togetherWith(slideOutHorizontally(tween(350)) { -it } + fadeOut(tween(200)))
                        },
                        label = "questionAnim"
                    ) { index ->
                        QuestionCard(question = state.questions[index])
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    AnimatedContent(
                        targetState = state.currentIndex,
                        transitionSpec = {
                            (fadeIn(tween(400)) + slideInHorizontally(tween(400)) { it / 2 })
                                .togetherWith(fadeOut(tween(200)))
                        },
                        label = "optionsAnim"
                    ) { index ->
                        AnswerOptions(
                            question = state.questions[index],
                            selectedAnswer = state.selectedAnswer,
                            correctIndex = state.questions[index].correctIndex,
                            onAnswerSelected = vm::selectAnswer
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    AnimatedVisibility(
                        visible = state.showFeedback,
                        enter = fadeIn(tween(300)) + expandVertically(tween(300))
                    ) {
                        FeedbackBanner(
                            message = state.feedbackMessage,
                            isCorrect = state.isCorrect
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    AnimatedVisibility(visible = state.showFeedback) {
                        val isLast = state.currentIndex == state.questions.size - 1
                        CosmicButton(
                            text = if (isLast) "See Results" else "Next Question",
                            gradient = Brush.horizontalGradient(listOf(SpacePurple, SpaceCyan.copy(0.8f))),
                            onClick = vm::nextQuestion,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun QuizProgressBar(current: Int, total: Int, score: Int) {
    val progress = (current + 1).toFloat() / total.toFloat()
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(600),
        label = "quizProgress"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Question ${current + 1} / $total",
                color = StarWhite.copy(alpha = 0.7f),
                fontSize = 13.sp
            )
            Text(
                "Score: $score",
                color = SpaceGold,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(CardBackground)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .background(
                        Brush.horizontalGradient(listOf(SpacePurple, SpaceCyan)),
                        RoundedCornerShape(4.dp)
                    )
            )
        }
    }
}

@Composable
fun QuestionCard(question: QuizQuestion) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 20.dp,
        borderColor = SpacePurple.copy(alpha = 0.5f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = question.question,
                color = StarWhite,
                fontSize = 19.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                lineHeight = 28.sp
            )
        }
    }
}

@Composable
fun AnswerOptions(
    question: QuizQuestion,
    selectedAnswer: Int?,
    correctIndex: Int,
    onAnswerSelected: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        question.options.forEachIndexed { index, option ->
            AnswerButton(
                text = option,
                index = index,
                selectedAnswer = selectedAnswer,
                correctIndex = correctIndex,
                onSelected = { onAnswerSelected(index) }
            )
        }
    }
}

@Composable
fun AnswerButton(
    text: String,
    index: Int,
    selectedAnswer: Int?,
    correctIndex: Int,
    onSelected: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy),
        label = "answerScale"
    )

    // Shake animation for wrong answer
    val shakeAnim = remember { Animatable(0f) }
    val isWrong = selectedAnswer == index && index != correctIndex
    LaunchedEffect(isWrong) {
        if (isWrong) {
            repeat(4) {
                shakeAnim.animateTo(8f, tween(50))
                shakeAnim.animateTo(-8f, tween(50))
            }
            shakeAnim.animateTo(0f, tween(50))
        }
    }

    LaunchedEffect(pressed) {
        if (pressed) { delay(120); pressed = false }
    }

    val (bgColor, borderColor, textColor) = when {
        selectedAnswer == null -> Triple(CardBackground, CardBorder.copy(0.5f), StarWhite)
        index == correctIndex  -> Triple(SpaceGreen.copy(0.2f), SpaceGreen, SpaceGreen)
        index == selectedAnswer -> Triple(SpaceRed.copy(0.2f), SpaceRed, SpaceRed)
        else -> Triple(CardBackground.copy(0.4f), CardBorder.copy(0.2f), StarWhite.copy(0.35f))
    }

    val optionLabel = listOf("A", "B", "C", "D")[index]

    Button(
        onClick = { if (selectedAnswer == null) { pressed = true; onSelected() } },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scale)
            .graphicsLayer { translationX = shakeAnim.value },
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor, RoundedCornerShape(14.dp))
                .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(borderColor.copy(0.2f), CircleShape)
                        .border(1.dp, borderColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(optionLabel, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Text(text, color = textColor, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun FeedbackBanner(message: String, isCorrect: Boolean) {
    val color = if (isCorrect) SpaceGreen else SpaceRed
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color.copy(0.15f), RoundedCornerShape(14.dp))
            .border(1.dp, color.copy(0.5f), RoundedCornerShape(14.dp))
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = color,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun QuizResultScreen(score: Int, total: Int, onRestart: () -> Unit, modifier: Modifier = Modifier) {
    val percentage = (score.toFloat() / total.toFloat() * 100).toInt()
    val message = when {
        percentage >= 90 -> "Cosmic Master!"
        percentage >= 70 -> "Star Explorer!"
        percentage >= 50 -> "Space Cadet!"
        else             -> "Keep Exploring!"
    }
    val emoji = when {
        percentage >= 90 -> "🏆"
        percentage >= 70 -> "🌟"
        percentage >= 50 -> "🚀"
        else             -> "🌙"
    }

    var visible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.4f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "resultScale"
    )
    LaunchedEffect(Unit) { visible = true }

    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(emoji, fontSize = 80.sp, modifier = Modifier.scale(scale))
        Spacer(modifier = Modifier.height(16.dp))
        Text(message, color = SpaceGold, fontSize = 30.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Final Score", color = StarWhite.copy(0.55f), fontSize = 15.sp)
        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .size(150.dp)
                .scale(scale)
                .background(
                    Brush.radialGradient(listOf(SpacePurple.copy(0.3f), Color.Transparent)),
                    CircleShape
                )
                .border(
                    2.dp,
                    Brush.sweepGradient(listOf(SpaceCyan, SpacePurpleLight, SpaceGold, SpaceCyan)),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$score/$total", color = StarWhite, fontSize = 38.sp, fontWeight = FontWeight.Black)
                Text("$percentage%", color = SpaceCyan, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
        CosmicButton(
            text = "Play Again",
            gradient = Brush.horizontalGradient(listOf(SpacePurple, SpaceCyan.copy(0.8f))),
            onClick = onRestart,
            icon = "🔄",
            modifier = Modifier.fillMaxWidth()
        )
    }
}

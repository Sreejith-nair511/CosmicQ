package com.example.cosmoq1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cosmoq1.data.QuizQuestion
import com.example.cosmoq1.data.quizQuestions
import com.example.cosmoq1.ui.components.SpaceGradientBackground
import com.example.cosmoq1.ui.theme.*
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
fun QuizScreen() {
    val context = LocalContext.current
    val questions = remember { quizQuestions.shuffled() }

    var currentIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    var showResult by remember { mutableStateOf(false) }
    var quizFinished by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf("") }

    SpaceGradientBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "🌟 Quiz Mode",
                            fontWeight = FontWeight.Bold,
                            color = StarWhite
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { (context as? QuizActivity)?.finish() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = SpacePurpleLight
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
            if (quizFinished) {
                QuizResultScreen(
                    score = score,
                    total = questions.size,
                    onRestart = {
                        currentIndex = 0
                        score = 0
                        selectedAnswer = null
                        showResult = false
                        quizFinished = false
                        feedbackMessage = ""
                    },
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

                    // Progress bar
                    QuizProgressBar(
                        current = currentIndex,
                        total = questions.size,
                        score = score
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Question card with slide animation
                    AnimatedContent(
                        targetState = currentIndex,
                        transitionSpec = {
                            (slideInHorizontally(tween(300)) { it } + fadeIn(tween(300)))
                                .togetherWith(slideOutHorizontally(tween(300)) { -it } + fadeOut(tween(300)))
                        },
                        label = "questionTransition"
                    ) { index ->
                        QuestionCard(question = questions[index])
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Answer options
                    AnimatedContent(
                        targetState = currentIndex,
                        transitionSpec = {
                            (fadeIn(tween(400)) + slideInHorizontally(tween(400)) { it / 2 })
                                .togetherWith(fadeOut(tween(200)))
                        },
                        label = "optionsTransition"
                    ) { index ->
                        AnswerOptions(
                            question = questions[index],
                            selectedAnswer = selectedAnswer,
                            onAnswerSelected = { answerIndex ->
                                if (selectedAnswer == null) {
                                    selectedAnswer = answerIndex
                                    val isCorrect = answerIndex == questions[index].correctIndex
                                    if (isCorrect) {
                                        score++
                                        feedbackMessage = "🎉 Correct!"
                                    } else {
                                        feedbackMessage = "❌ Wrong! The answer was: ${questions[index].options[questions[index].correctIndex]}"
                                    }
                                    showResult = true
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Feedback message
                    AnimatedVisibility(
                        visible = showResult,
                        enter = fadeIn(tween(300)) + slideInHorizontally(tween(300)) { -it / 2 }
                    ) {
                        val isCorrect = selectedAnswer == questions[currentIndex].correctIndex
                        FeedbackBanner(
                            message = feedbackMessage,
                            isCorrect = isCorrect
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Next button
                    AnimatedVisibility(visible = showResult) {
                        val isLast = currentIndex == questions.size - 1
                        CosmicButton(
                            text = if (isLast) "See Results 🏆" else "Next Question →",
                            gradient = Brush.horizontalGradient(
                                colors = listOf(SpacePurple, SpaceCyan.copy(alpha = 0.7f))
                            ),
                            onClick = {
                                if (isLast) {
                                    quizFinished = true
                                } else {
                                    currentIndex++
                                    selectedAnswer = null
                                    showResult = false
                                    feedbackMessage = ""
                                }
                            },
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
    val progress = current.toFloat() / total.toFloat()
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(500),
        label = "progress"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Question ${current + 1} of $total",
                color = StarWhite.copy(alpha = 0.7f),
                fontSize = 13.sp
            )
            Text(
                text = "Score: $score ⭐",
                color = SpaceGold,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(CardBackground)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .background(
                        Brush.horizontalGradient(listOf(SpacePurple, SpaceCyan)),
                        RoundedCornerShape(3.dp)
                    )
            )
        }
    }
}

@Composable
fun QuestionCard(question: QuizQuestion) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardBackground, RoundedCornerShape(20.dp))
            .border(
                1.dp,
                Brush.horizontalGradient(
                    listOf(SpacePurple.copy(alpha = 0.5f), SpaceCyan.copy(alpha = 0.3f))
                ),
                RoundedCornerShape(20.dp)
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = question.question,
            color = StarWhite,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            lineHeight = 30.sp
        )
    }
}

@Composable
fun AnswerOptions(
    question: QuizQuestion,
    selectedAnswer: Int?,
    onAnswerSelected: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        question.options.forEachIndexed { index, option ->
            AnswerButton(
                text = option,
                index = index,
                selectedAnswer = selectedAnswer,
                correctIndex = question.correctIndex,
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
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy),
        label = "answerScale"
    )

    LaunchedEffect(pressed) {
        if (pressed) {
            delay(120)
            pressed = false
        }
    }

    val (bgColor, borderColor, textColor) = when {
        selectedAnswer == null -> Triple(
            CardBackground,
            CardBorder.copy(alpha = 0.5f),
            StarWhite
        )
        index == correctIndex -> Triple(
            SpaceGreen.copy(alpha = 0.2f),
            SpaceGreen,
            SpaceGreen
        )
        index == selectedAnswer -> Triple(
            SpaceRed.copy(alpha = 0.2f),
            SpaceRed,
            SpaceRed
        )
        else -> Triple(
            CardBackground.copy(alpha = 0.5f),
            CardBorder.copy(alpha = 0.2f),
            StarWhite.copy(alpha = 0.4f)
        )
    }

    val optionLabel = listOf("A", "B", "C", "D")[index]

    Button(
        onClick = {
            if (selectedAnswer == null) {
                pressed = true
                onSelected()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .scale(scale),
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
                        .size(28.dp)
                        .background(borderColor.copy(alpha = 0.2f), CircleShape)
                        .border(1.dp, borderColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = optionLabel,
                        color = textColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = text,
                    color = textColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
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
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = color,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun QuizResultScreen(
    score: Int,
    total: Int,
    onRestart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val percentage = (score.toFloat() / total.toFloat() * 100).toInt()
    val (emoji, message) = when {
        percentage >= 90 -> "🏆" to "Cosmic Master!"
        percentage >= 70 -> "🌟" to "Star Explorer!"
        percentage >= 50 -> "🚀" to "Space Cadet!"
        else             -> "🌙" to "Keep Exploring!"
    }

    var visible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.5f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "resultScale"
    )

    LaunchedEffect(Unit) { visible = true }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = emoji,
            fontSize = 80.sp,
            modifier = Modifier.scale(scale)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            color = SpaceGold,
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "You scored",
            color = StarWhite.copy(alpha = 0.6f),
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Score circle
        Box(
            modifier = Modifier
                .size(140.dp)
                .scale(scale)
                .background(
                    Brush.radialGradient(
                        listOf(SpacePurple.copy(alpha = 0.3f), Color.Transparent)
                    ),
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
                Text(
                    text = "$score/$total",
                    color = StarWhite,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "$percentage%",
                    color = SpaceCyan,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        CosmicButton(
            text = "🔄  Play Again",
            gradient = Brush.horizontalGradient(
                listOf(SpacePurple, SpaceCyan.copy(alpha = 0.8f))
            ),
            onClick = onRestart,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

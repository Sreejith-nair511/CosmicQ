package com.example.cosmoq1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cosmoq1.data.QuizQuestion
import com.example.cosmoq1.data.quizQuestions
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class QuizUiState(
    val questions: List<QuizQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val score: Int = 0,
    val selectedAnswer: Int? = null,
    val showFeedback: Boolean = false,
    val isCorrect: Boolean = false,
    val isFinished: Boolean = false,
    val feedbackMessage: String = ""
)

class QuizViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    init {
        startQuiz()
    }

    fun startQuiz() {
        _uiState.value = QuizUiState(questions = quizQuestions.shuffled())
    }

    fun selectAnswer(answerIndex: Int) {
        val state = _uiState.value
        if (state.selectedAnswer != null) return
        val currentQuestion = state.questions[state.currentIndex]
        val isCorrect = answerIndex == currentQuestion.correctIndex
        val newScore = if (isCorrect) state.score + 1 else state.score
        val message = if (isCorrect) "Correct! Well done!" else "Wrong! Answer: ${currentQuestion.options[currentQuestion.correctIndex]}"

        _uiState.value = state.copy(
            selectedAnswer = answerIndex,
            showFeedback = true,
            isCorrect = isCorrect,
            score = newScore,
            feedbackMessage = message
        )
    }

    fun nextQuestion() {
        val state = _uiState.value
        val nextIndex = state.currentIndex + 1
        if (nextIndex >= state.questions.size) {
            _uiState.value = state.copy(isFinished = true)
        } else {
            _uiState.value = state.copy(
                currentIndex = nextIndex,
                selectedAnswer = null,
                showFeedback = false,
                feedbackMessage = ""
            )
        }
    }

    fun restartQuiz() {
        startQuiz()
    }
}

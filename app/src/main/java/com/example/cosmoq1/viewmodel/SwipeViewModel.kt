package com.example.cosmoq1.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cosmoq1.data.SwipeCard
import com.example.cosmoq1.db.AppDatabase
import com.example.cosmoq1.repository.SwipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SwipeUiState {
    object Loading : SwipeUiState()
    data class Success(val cards: List<SwipeCard>) : SwipeUiState()
    data class Error(val message: String) : SwipeUiState()
}

class SwipeViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = SwipeRepository(AppDatabase.getInstance(application))

    private val _uiState = MutableStateFlow<SwipeUiState>(SwipeUiState.Loading)
    val uiState: StateFlow<SwipeUiState> = _uiState.asStateFlow()

    private val _savedCards = MutableStateFlow<List<SwipeCard>>(emptyList())
    val savedCards: StateFlow<List<SwipeCard>> = _savedCards.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    init {
        loadCards()
        observeSaved()
    }

    fun loadCards() {
        viewModelScope.launch {
            _uiState.value = SwipeUiState.Loading
            try {
                val cards = repo.fetchCards()
                _uiState.value = if (cards.isEmpty())
                    SwipeUiState.Error("No content available. Pull to refresh.")
                else
                    SwipeUiState.Success(cards)
            } catch (e: Exception) {
                _uiState.value = SwipeUiState.Error(e.message ?: "Failed to load content.")
            }
        }
    }

    fun onSwipeRight(card: SwipeCard) {
        viewModelScope.launch {
            repo.saveCard(card)
            _toastMessage.value = "Saved: ${card.title.take(30)}..."
        }
    }

    fun onSwipeLeft(card: SwipeCard) {
        // Skip — no action needed
    }

    fun removeFromSaved(id: String) {
        viewModelScope.launch { repo.removeCard(id) }
    }

    fun clearToast() { _toastMessage.value = null }

    private fun observeSaved() {
        viewModelScope.launch {
            repo.getSavedCards().collect { _savedCards.value = it }
        }
    }
}

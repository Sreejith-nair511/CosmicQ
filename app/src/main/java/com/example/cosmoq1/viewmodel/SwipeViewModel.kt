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

    // Pagination offset for infinite loading
    private var currentOffset = 0
    private val allCards = mutableListOf<SwipeCard>()

    init {
        loadCards()
        observeSaved()
    }

    fun loadCards() {
        viewModelScope.launch {
            _uiState.value = SwipeUiState.Loading
            currentOffset = 0
            allCards.clear()
            try {
                val cards = repo.fetchCards(offset = 0)
                allCards.addAll(cards)
                _uiState.value = if (allCards.isEmpty())
                    SwipeUiState.Error("No content available. Pull to refresh.")
                else
                    SwipeUiState.Success(allCards.toList())
            } catch (e: Exception) {
                _uiState.value = SwipeUiState.Error(e.message ?: "Failed to load content.")
            }
        }
    }

    fun loadMoreCards() {
        viewModelScope.launch {
            currentOffset += 100
            try {
                val more = repo.fetchCards(offset = currentOffset)
                allCards.addAll(more)
                _uiState.value = SwipeUiState.Success(allCards.toList())
            } catch (_: Exception) {
                // Silently fail — existing cards still shown
            }
        }
    }

    fun onSwipeRight(card: SwipeCard) {
        viewModelScope.launch {
            repo.saveCard(card)
            _toastMessage.value = "Saved: ${card.title.take(30)}..."
        }
    }

    fun onSwipeLeft(card: SwipeCard) { /* skip */ }

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

package com.example.cosmoq1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cosmoq1.data.ApodResponse
import com.example.cosmoq1.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

sealed class ApodUiState {
    object Loading : ApodUiState()
    data class Success(val data: ApodResponse) : ApodUiState()
    data class Error(val message: String) : ApodUiState()
}

class DailySpaceViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<ApodUiState>(ApodUiState.Loading)
    val uiState: StateFlow<ApodUiState> = _uiState.asStateFlow()

    // Cache: date string -> ApodResponse so it only fetches once per calendar day
    private var cachedDate: String = ""
    private var cachedApod: ApodResponse? = null

    init {
        fetchApod()
    }

    fun fetchApod() {
        viewModelScope.launch {
            val today = LocalDate.now().toString()
            // Return cached result if same calendar day
            if (today == cachedDate && cachedApod != null) {
                _uiState.value = ApodUiState.Success(cachedApod!!)
                return@launch
            }
            _uiState.value = ApodUiState.Loading
            try {
                val response = RetrofitClient.nasaApi.getApod()
                cachedDate = today
                cachedApod = response
                _uiState.value = ApodUiState.Success(response)
            } catch (e: Exception) {
                _uiState.value = ApodUiState.Error(
                    e.message ?: "Failed to load NASA picture of the day"
                )
            }
        }
    }

    fun forceRefresh() {
        cachedDate = ""
        cachedApod = null
        fetchApod()
    }
}

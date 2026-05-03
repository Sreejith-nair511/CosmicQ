package com.example.cosmoq1.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cosmoq1.data.ThemePreference
import com.example.cosmoq1.ui.theme.AppTheme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    val currentTheme: StateFlow<AppTheme> = ThemePreference
        .getTheme(application)
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppTheme.DEEP_SPACE)

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            ThemePreference.saveTheme(getApplication(), theme)
        }
    }
}

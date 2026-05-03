package com.example.cosmoq1.viewmodel

import androidx.lifecycle.ViewModel
import com.example.cosmoq1.data.Planet
import com.example.cosmoq1.data.samplePlanets
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ExploreViewModel : ViewModel() {
    private val _planets = MutableStateFlow<List<Planet>>(samplePlanets)
    val planets: StateFlow<List<Planet>> = _planets.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filteredPlanets = MutableStateFlow<List<Planet>>(samplePlanets)
    val filteredPlanets: StateFlow<List<Planet>> = _filteredPlanets.asStateFlow()

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        _filteredPlanets.value = if (query.isBlank()) {
            samplePlanets
        } else {
            samplePlanets.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.type.contains(query, ignoreCase = true)
            }
        }
    }
}

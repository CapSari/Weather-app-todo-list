package com.dve.sari.todolistapp.presentation.weather

import com.dve.sari.todolistapp.domain.model.WeatherData

sealed class WeatherUiState {
    data object Initial : WeatherUiState()

    data object Loading : WeatherUiState()

    data class Success(val data: WeatherData) : WeatherUiState()

    data class Error(val message: String) : WeatherUiState()

    data object LocationPermissionDenied : WeatherUiState()
}

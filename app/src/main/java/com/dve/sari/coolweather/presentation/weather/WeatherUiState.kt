package com.dve.sari.coolweather.presentation.weather

import com.dve.sari.coolweather.domain.model.WeatherData

sealed class WeatherUiState {
    data object Initial : WeatherUiState()

    data object Loading : WeatherUiState()

    data class Success(val data: WeatherData) : WeatherUiState()

    data class Error(val message: String) : WeatherUiState()

    data object LocationPermissionDenied : WeatherUiState()
}

package com.dve.sari.todolistapp.presentation.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dve.sari.todolistapp.domain.usecase.GetWeatherUseCase
import com.dve.sari.todolistapp.domain.usecase.RefreshWeatherUseCase
import com.dve.sari.todolistapp.util.LocationManager
import com.dve.sari.todolistapp.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val refreshWeatherUseCase: RefreshWeatherUseCase,
    private val locationManager: LocationManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Initial)
    private val _isRefreshing = MutableStateFlow(false)

    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        observeWeatherData()
    }

    private fun observeWeatherData() {
        viewModelScope.launch {
            getWeatherUseCase()
                .catch { exception ->
                    _uiState.value = WeatherUiState.Error(
                        exception.localizedMessage ?: "Failed to observe weather data"
                    )
                }
                .collect { weatherData ->
                    if (weatherData != null) {
                        _uiState.value = WeatherUiState.Success(weatherData)
                    } else {
                        // No cached data available, show initial state
                        if (_uiState.value !is WeatherUiState.Error &&
                            _uiState.value !is WeatherUiState.LocationPermissionDenied) {
                            _uiState.value = WeatherUiState.Initial
                        }
                    }
                    _isRefreshing.value = false
                }
        }
    }

    fun fetchWeatherData() {
        viewModelScope.launch {
            // Check location permissions first
            if (!locationManager.hasLocationPermission()) {
                _uiState.value = WeatherUiState.LocationPermissionDenied
                return@launch
            }

            // Show loading state (or refreshing if we already have data)
            if (_uiState.value is WeatherUiState.Success) {
                _isRefreshing.value = true
            } else {
                _uiState.value = WeatherUiState.Loading
            }

            // Refresh data from network
            when (val result = refreshWeatherUseCase()) {
                is Result.Success -> {
                    _isRefreshing.value = false
                }
                is Result.Error -> {
                    _isRefreshing.value = false
                    if (_uiState.value !is WeatherUiState.Success) {
                        _uiState.value = WeatherUiState.Error(result.message)
                    }
                }
                is Result.Loading -> {
                }
            }
        }
    }

    fun refresh() {
        fetchWeatherData()
    }

    fun onLocationPermissionResult(granted: Boolean) {
        if (granted) {
            fetchWeatherData()
        } else {
            _uiState.value = WeatherUiState.LocationPermissionDenied
        }
    }
}

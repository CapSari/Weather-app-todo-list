package com.dve.sari.coolweather.domain.repository

import com.dve.sari.coolweather.domain.model.WeatherData
import com.dve.sari.coolweather.util.Result
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {

    fun observeWeather(): Flow<WeatherData?>

    suspend fun refreshWeather(): Result<Unit>

    suspend fun getWeatherByLocation(
        latitude: Double,
        longitude: Double
    ): Result<WeatherData>
}

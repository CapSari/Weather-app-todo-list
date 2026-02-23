package com.dve.sari.coolweather.domain.usecase

import com.dve.sari.coolweather.domain.model.WeatherData
import com.dve.sari.coolweather.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow

class GetWeatherUseCase(
    private val repository: WeatherRepository
) {

    operator fun invoke(): Flow<WeatherData?> {
        return repository.observeWeather()
    }
}

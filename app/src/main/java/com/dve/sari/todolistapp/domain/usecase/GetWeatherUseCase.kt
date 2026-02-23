package com.dve.sari.todolistapp.domain.usecase

import com.dve.sari.todolistapp.domain.model.WeatherData
import com.dve.sari.todolistapp.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow

class GetWeatherUseCase(
    private val repository: WeatherRepository
) {

    operator fun invoke(): Flow<WeatherData?> {
        return repository.observeWeather()
    }
}

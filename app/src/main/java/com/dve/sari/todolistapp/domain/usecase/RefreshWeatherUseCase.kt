package com.dve.sari.todolistapp.domain.usecase

import com.dve.sari.todolistapp.domain.repository.WeatherRepository
import com.dve.sari.todolistapp.util.Result

class RefreshWeatherUseCase(
    private val repository: WeatherRepository
) {

    suspend operator fun invoke(): Result<Unit> {
        return repository.refreshWeather()
    }
}

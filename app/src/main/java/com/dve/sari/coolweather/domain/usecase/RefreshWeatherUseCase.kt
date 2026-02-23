package com.dve.sari.coolweather.domain.usecase

import com.dve.sari.coolweather.domain.repository.WeatherRepository
import com.dve.sari.coolweather.util.Result

class RefreshWeatherUseCase(
    private val repository: WeatherRepository
) {

    suspend operator fun invoke(): Result<Unit> {
        return repository.refreshWeather()
    }
}

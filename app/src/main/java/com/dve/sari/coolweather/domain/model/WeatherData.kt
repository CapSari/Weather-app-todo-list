package com.dve.sari.coolweather.domain.model

data class WeatherData(
    val temperature: Double,
    val temperatureUnit: String = "°C",
    val description: String,
    val iconCode: String,
    val cityName: String,
    val humidity: Int,
    val windSpeed: Double,
    val sunrise: Long,
    val sunset: Long,
    val lastUpdated: Long = System.currentTimeMillis()
)

package com.dve.sari.todolistapp.data.local.entity

import com.dve.sari.todolistapp.domain.model.WeatherData

fun WeatherEntity.toDomain(): WeatherData {
    return WeatherData(
        temperature = this.temperature,
        temperatureUnit = this.temperatureUnit,
        description = this.description,
        iconCode = this.iconCode,
        cityName = this.cityName,
        humidity = this.humidity,
        windSpeed = this.windSpeed,
        sunrise = this.sunrise,
        sunset = this.sunset,
        lastUpdated = this.timestamp
    )
}

fun WeatherData.toEntity(
    latitude: Double,
    longitude: Double,
    timestamp: Long = System.currentTimeMillis()
): WeatherEntity {
    return WeatherEntity(
        latitude = latitude,
        longitude = longitude,
        temperature = this.temperature,
        temperatureUnit = this.temperatureUnit,
        description = this.description,
        iconCode = this.iconCode,
        cityName = this.cityName,
        humidity = this.humidity,
        windSpeed = this.windSpeed,
        sunrise = this.sunrise,
        sunset = this.sunset,
        timestamp = timestamp
    )
}

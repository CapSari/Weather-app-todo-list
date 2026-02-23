package com.dve.sari.todolistapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather")
data class WeatherEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val temperature: Double,
    val temperatureUnit: String,
    val description: String,
    val iconCode: String,
    val cityName: String,
    val humidity: Int,
    val windSpeed: Double,
    val sunrise: Long,
    val sunset: Long,
    val timestamp: Long
)

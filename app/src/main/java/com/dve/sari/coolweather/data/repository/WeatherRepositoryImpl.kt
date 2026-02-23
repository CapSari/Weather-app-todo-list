package com.dve.sari.coolweather.data.repository

import com.dve.sari.coolweather.data.api.WeatherApiService
import com.dve.sari.coolweather.data.local.dao.WeatherDao
import com.dve.sari.coolweather.data.local.entity.toDomain
import com.dve.sari.coolweather.data.local.entity.toEntity
import com.dve.sari.coolweather.domain.model.WeatherData
import com.dve.sari.coolweather.domain.repository.WeatherRepository
import com.dve.sari.coolweather.util.LocationManager
import com.dve.sari.coolweather.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException

class WeatherRepositoryImpl(
    private val apiService: WeatherApiService,
    private val weatherDao: WeatherDao,
    private val locationManager: LocationManager,
    private val apiKey: String
) : WeatherRepository {

    override fun observeWeather(): Flow<WeatherData?> {
        return weatherDao.getLatestWeather().map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun refreshWeather(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                if (!locationManager.hasLocationPermission()) {
                    return@withContext Result.Error("Location permission not granted")
                }

                val locationResult = locationManager.getCurrentLocation()
                if (locationResult is Result.Error) {
                    return@withContext Result.Error(locationResult.message)
                }

                val (latitude, longitude) = (locationResult as Result.Success).data

                val weatherResult = getWeatherByLocation(latitude, longitude)
                if (weatherResult is Result.Error) {
                    return@withContext Result.Error(weatherResult.message)
                }

                val weatherData = (weatherResult as Result.Success).data

                val entity = weatherData.toEntity(latitude, longitude)
                weatherDao.insertWeather(entity)

                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(
                    message = "Failed to refresh weather: ${e.localizedMessage ?: "Unknown error"}",
                    exception = e
                )
            }
        }
    }

    override suspend fun getWeatherByLocation(
        latitude: Double,
        longitude: Double
    ): Result<WeatherData> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getWeatherByCoordinates(
                    latitude = latitude,
                    longitude = longitude,
                    apiKey = apiKey,
                    units = "metric"
                )

                val weatherData = WeatherData(
                    temperature = response.main.temperature,
                    temperatureUnit = "°C",
                    description = response.weather.firstOrNull()?.description?.capitalize() ?: "Unknown",
                    iconCode = response.weather.firstOrNull()?.icon ?: "",
                    cityName = response.cityName,
                    humidity = response.main.humidity,
                    windSpeed = response.wind.speed,
                    sunrise = response.system.sunrise,
                    sunset = response.system.sunset
                )

                Result.Success(weatherData)
            } catch (e: IOException) {
                Result.Error(
                    message = "Network error. Please check your internet connection.",
                    exception = e
                )
            } catch (e: Exception) {
                Result.Error(
                    message = "Failed to fetch weather data: ${e.localizedMessage ?: "Unknown error"}",
                    exception = e
                )
            }
        }
    }

    private fun String.capitalize(): String {
        return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
}

package com.dve.sari.coolweather.data.repository

import app.cash.turbine.test
import com.dve.sari.coolweather.data.api.Clouds
import com.dve.sari.coolweather.data.api.Coordinates
import com.dve.sari.coolweather.data.api.Main
import com.dve.sari.coolweather.data.api.System
import com.dve.sari.coolweather.data.api.Weather
import com.dve.sari.coolweather.data.api.WeatherApiService
import com.dve.sari.coolweather.data.api.WeatherResponse
import com.dve.sari.coolweather.data.api.Wind
import com.dve.sari.coolweather.data.local.dao.WeatherDao
import com.dve.sari.coolweather.data.local.entity.WeatherEntity
import com.dve.sari.coolweather.util.LocationManager
import com.dve.sari.coolweather.util.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class WeatherRepositoryImplTest {

    private lateinit var apiService: WeatherApiService
    private lateinit var weatherDao: WeatherDao
    private lateinit var locationManager: LocationManager
    private lateinit var repository: WeatherRepositoryImpl
    private val testApiKey = "test_api_key"

    @Before
    fun setup() {
        apiService = mockk()
        weatherDao = mockk()
        locationManager = mockk()
        repository = WeatherRepositoryImpl(apiService, weatherDao, locationManager, testApiKey)
    }

    @Test
    fun `observeWeather emits cached data from database`() = runTest {
        // Given
        val cachedEntity = WeatherEntity(
            id = 1,
            latitude = 40.7128,
            longitude = -74.0060,
            temperature = 20.5,
            temperatureUnit = "°C",
            description = "Clear sky",
            iconCode = "01d",
            cityName = "New York",
            humidity = 65,
            windSpeed = 5.5,
            sunrise = 1609416000L,
            sunset = 1609452000L,
            timestamp = 1234567890L
        )

        every { weatherDao.getLatestWeather() } returns flowOf(cachedEntity)

        // When & Then
        repository.observeWeather().test {
            val weatherData = awaitItem()
            assertEquals("New York", weatherData?.cityName)
            assertEquals(20.5, weatherData?.temperature ?: 0.0, 0.01)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `observeWeather emits null when no cached data exists`() = runTest {
        // Given
        every { weatherDao.getLatestWeather() } returns flowOf(null)

        // When & Then
        repository.observeWeather().test {
            val weatherData = awaitItem()
            assertEquals(null, weatherData)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `refreshWeather fetches from API and saves to database`() = runTest {
        // Given
        val latitude = 40.7128
        val longitude = -74.0060
        val mockResponse = createMockWeatherResponse()

        every { locationManager.hasLocationPermission() } returns true
        coEvery { locationManager.getCurrentLocation() } returns Result.Success(Pair(latitude, longitude))
        coEvery {
            apiService.getWeatherByCoordinates(
                latitude = latitude,
                longitude = longitude,
                apiKey = testApiKey,
                units = "metric"
            )
        } returns mockResponse
        coEvery { weatherDao.insertWeather(any()) } returns Unit

        // When
        val result = repository.refreshWeather()

        // Then
        assertTrue(result is Result.Success)
        coVerify(exactly = 1) { weatherDao.insertWeather(any()) }
    }

    @Test
    fun `refreshWeather returns error when location permission denied`() = runTest {
        // Given
        every { locationManager.hasLocationPermission() } returns false

        // When
        val result = repository.refreshWeather()

        // Then
        assertTrue(result is Result.Error)
        assertEquals("Location permission not granted", (result as Result.Error).message)
    }

    @Test
    fun `refreshWeather returns error when location fetch fails`() = runTest {
        // Given
        every { locationManager.hasLocationPermission() } returns true
        coEvery { locationManager.getCurrentLocation() } returns Result.Error("Location error")

        // When
        val result = repository.refreshWeather()

        // Then
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).message.contains("Location error"))
    }

    @Test
    fun `getWeatherByLocation returns success when API call succeeds`() = runTest {
        // Given
        val latitude = 40.7128
        val longitude = -74.0060
        val mockResponse = createMockWeatherResponse()

        coEvery {
            apiService.getWeatherByCoordinates(
                latitude = latitude,
                longitude = longitude,
                apiKey = testApiKey,
                units = "metric"
            )
        } returns mockResponse

        // When
        val result = repository.getWeatherByLocation(latitude, longitude)

        // Then
        assertTrue(result is Result.Success)
        val weatherData = (result as Result.Success).data
        assertEquals(20.5, weatherData.temperature, 0.01)
        assertEquals("Clear sky", weatherData.description)
        assertEquals("New York", weatherData.cityName)
        assertEquals(65, weatherData.humidity)
        assertEquals(5.5, weatherData.windSpeed, 0.01)

        coVerify(exactly = 1) {
            apiService.getWeatherByCoordinates(
                latitude = latitude,
                longitude = longitude,
                apiKey = testApiKey,
                units = "metric"
            )
        }
    }

    @Test
    fun `getWeatherByLocation returns error when network error occurs`() = runTest {
        // Given
        val latitude = 40.7128
        val longitude = -74.0060

        coEvery {
            apiService.getWeatherByCoordinates(
                latitude = any(),
                longitude = any(),
                apiKey = any(),
                units = any()
            )
        } throws IOException("Network error")

        // When
        val result = repository.getWeatherByLocation(latitude, longitude)

        // Then
        assertTrue(result is Result.Error)
        val errorMessage = (result as Result.Error).message
        assertTrue(errorMessage.contains("Network error"))
    }

    @Test
    fun `getWeatherByLocation returns error when API throws exception`() = runTest {
        // Given
        val latitude = 40.7128
        val longitude = -74.0060

        coEvery {
            apiService.getWeatherByCoordinates(
                latitude = any(),
                longitude = any(),
                apiKey = any(),
                units = any()
            )
        } throws RuntimeException("API error")

        // When
        val result = repository.getWeatherByLocation(latitude, longitude)

        // Then
        assertTrue(result is Result.Error)
        val errorMessage = (result as Result.Error).message
        assertTrue(errorMessage.contains("Failed to fetch weather data"))
    }

    @Test
    fun `getWeatherByLocation handles empty weather list gracefully`() = runTest {
        // Given
        val latitude = 40.7128
        val longitude = -74.0060
        val mockResponse = createMockWeatherResponse(emptyWeatherList = true)

        coEvery {
            apiService.getWeatherByCoordinates(
                latitude = any(),
                longitude = any(),
                apiKey = any(),
                units = any()
            )
        } returns mockResponse

        // When
        val result = repository.getWeatherByLocation(latitude, longitude)

        // Then
        assertTrue(result is Result.Success)
        val weatherData = (result as Result.Success).data
        assertEquals("Unknown", weatherData.description)
        assertEquals("", weatherData.iconCode)
    }

    private fun createMockWeatherResponse(emptyWeatherList: Boolean = false): WeatherResponse {
        return WeatherResponse(
            coordinates = Coordinates(longitude = -74.0060, latitude = 40.7128),
            weather = if (emptyWeatherList) emptyList() else listOf(
                Weather(
                    id = 800,
                    main = "Clear",
                    description = "clear sky",
                    icon = "01d"
                )
            ),
            base = "stations",
            main = Main(
                temperature = 20.5,
                feelsLike = 19.5,
                tempMin = 18.0,
                tempMax = 22.0,
                pressure = 1013,
                humidity = 65
            ),
            visibility = 10000,
            wind = Wind(speed = 5.5, direction = 180),
            clouds = Clouds(cloudiness = 0),
            timestamp = 1609459200,
            system = System(
                type = 1,
                id = 1234,
                country = "US",
                sunrise = 1609416000,
                sunset = 1609452000
            ),
            timezone = -18000,
            cityId = 5128581,
            cityName = "New York",
            code = 200
        )
    }
}

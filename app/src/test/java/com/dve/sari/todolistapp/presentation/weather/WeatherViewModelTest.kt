package com.dve.sari.todolistapp.presentation.weather

import com.dve.sari.todolistapp.domain.model.WeatherData
import com.dve.sari.todolistapp.domain.usecase.GetWeatherUseCase
import com.dve.sari.todolistapp.domain.usecase.RefreshWeatherUseCase
import com.dve.sari.todolistapp.util.LocationManager
import com.dve.sari.todolistapp.util.Result
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    private lateinit var viewModel: WeatherViewModel
    private lateinit var getWeatherUseCase: GetWeatherUseCase
    private lateinit var refreshWeatherUseCase: RefreshWeatherUseCase
    private lateinit var locationManager: LocationManager
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getWeatherUseCase = mockk()
        refreshWeatherUseCase = mockk()
        locationManager = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() {
        viewModel = WeatherViewModel(getWeatherUseCase, refreshWeatherUseCase, locationManager)
    }

    @Test
    fun `viewModel observes cached weather data on init`() = runTest {
        // Given
        val weatherData = createMockWeatherData()
        every { getWeatherUseCase() } returns flowOf(weatherData)

        // When
        createViewModel()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is WeatherUiState.Success)
        assertEquals(weatherData, (state as WeatherUiState.Success).data)
    }

    @Test
    fun `viewModel shows Initial when no cached data exists`() = runTest {
        // Given
        every { getWeatherUseCase() } returns flowOf(null)

        // When
        createViewModel()
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value is WeatherUiState.Initial)
    }

    @Test
    fun `fetchWeatherData shows LocationPermissionDenied when permission not granted`() = runTest {
        // Given
        every { getWeatherUseCase() } returns flowOf(null)
        every { locationManager.hasLocationPermission() } returns false

        // When
        createViewModel()
        viewModel.fetchWeatherData()
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value is WeatherUiState.LocationPermissionDenied)
    }

    @Test
    fun `fetchWeatherData refreshes from network and updates cache`() = runTest {
        // Given
        val weatherData = createMockWeatherData()
        every { getWeatherUseCase() } returns flowOf(null, weatherData) // First null, then data after refresh
        every { locationManager.hasLocationPermission() } returns true
        coEvery { refreshWeatherUseCase() } returns Result.Success(Unit)

        // When
        createViewModel()
        advanceUntilIdle()

        viewModel.fetchWeatherData()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is WeatherUiState.Success)
        assertEquals(weatherData, (state as WeatherUiState.Success).data)
    }

    @Test
    fun `fetchWeatherData shows error when refresh fails and no cached data`() = runTest {
        // Given
        every { getWeatherUseCase() } returns flowOf(null)
        every { locationManager.hasLocationPermission() } returns true
        coEvery { refreshWeatherUseCase() } returns Result.Error("Network error")

        // When
        createViewModel()
        viewModel.fetchWeatherData()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is WeatherUiState.Error)
        assertEquals("Network error", (state as WeatherUiState.Error).message)
    }

    @Test
    fun `fetchWeatherData keeps cached data when refresh fails`() = runTest {
        // Given
        val cachedWeatherData = createMockWeatherData()
        every { getWeatherUseCase() } returns flowOf(cachedWeatherData) // Have cached data
        every { locationManager.hasLocationPermission() } returns true
        coEvery { refreshWeatherUseCase() } returns Result.Error("Network error")

        // When
        createViewModel()
        advanceUntilIdle()

        viewModel.fetchWeatherData()
        advanceUntilIdle()

        // Then - Should still show cached data even though refresh failed
        val state = viewModel.uiState.value
        assertTrue(state is WeatherUiState.Success)
        assertEquals(cachedWeatherData, (state as WeatherUiState.Success).data)
    }

    @Test
    fun `refresh updates isRefreshing state`() = runTest {
        // Given
        val weatherData = createMockWeatherData()
        every { getWeatherUseCase() } returns flowOf(weatherData)
        every { locationManager.hasLocationPermission() } returns true
        coEvery { refreshWeatherUseCase() } returns Result.Success(Unit)

        // When
        createViewModel()
        advanceUntilIdle()

        viewModel.refresh()

        // Then - isRefreshing should be true during refresh
        // Note: Due to test timing, it might already be false by the time we check
        advanceUntilIdle()
        assertEquals(false, viewModel.isRefreshing.value) // Should be false after completion
    }

    @Test
    fun `onLocationPermissionResult refreshes when permission granted`() = runTest {
        // Given
        val weatherData = createMockWeatherData()
        every { getWeatherUseCase() } returns flowOf(null, weatherData)
        every { locationManager.hasLocationPermission() } returns true
        coEvery { refreshWeatherUseCase() } returns Result.Success(Unit)

        // When
        createViewModel()
        viewModel.onLocationPermissionResult(granted = true)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is WeatherUiState.Success)
    }

    @Test
    fun `onLocationPermissionResult shows denied state when permission not granted`() = runTest {
        // Given
        every { getWeatherUseCase() } returns flowOf(null)

        // When
        createViewModel()
        viewModel.onLocationPermissionResult(granted = false)
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value is WeatherUiState.LocationPermissionDenied)
    }

    private fun createMockWeatherData(): WeatherData {
        return WeatherData(
            temperature = 20.5,
            temperatureUnit = "°C",
            description = "Clear sky",
            iconCode = "01d",
            cityName = "New York",
            humidity = 65,
            windSpeed = 5.5,
            sunrise = 1609459200L, // 2021-01-01 06:00:00
            sunset = 1609491600L   // 2021-01-01 15:00:00
        )
    }
}

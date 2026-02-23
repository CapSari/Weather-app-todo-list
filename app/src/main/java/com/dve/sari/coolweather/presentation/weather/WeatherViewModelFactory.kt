package com.dve.sari.coolweather.presentation.weather

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dve.sari.coolweather.data.api.RetrofitClient
import com.dve.sari.coolweather.data.local.WeatherDatabase
import com.dve.sari.coolweather.data.repository.WeatherRepositoryImpl
import com.dve.sari.coolweather.domain.usecase.GetWeatherUseCase
import com.dve.sari.coolweather.domain.usecase.RefreshWeatherUseCase
import com.dve.sari.coolweather.util.LocationManager

class WeatherViewModelFactory(
    private val context: Context,
    private val apiKey: String
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            val database = WeatherDatabase.getDatabase(context)
            val weatherDao = database.weatherDao()
            val locationManager = LocationManager(context)

            val repository = WeatherRepositoryImpl(
                apiService = RetrofitClient.weatherApiService,
                weatherDao = weatherDao,
                locationManager = locationManager,
                apiKey = apiKey
            )

            val getWeatherUseCase = GetWeatherUseCase(repository)
            val refreshWeatherUseCase = RefreshWeatherUseCase(repository)

            return WeatherViewModel(
                getWeatherUseCase = getWeatherUseCase,
                refreshWeatherUseCase = refreshWeatherUseCase,
                locationManager = locationManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

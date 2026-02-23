package com.dve.sari.todolistapp.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("weather")
    suspend fun getWeatherByCoordinates(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse

    companion object {
        const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
        const val ICON_BASE_URL = "https://openweathermap.org/img/wn/"
        const val ICON_SIZE = "@2x.png"

        fun getIconUrl(iconCode: String): String {
            return "$ICON_BASE_URL$iconCode$ICON_SIZE"
        }
    }
}

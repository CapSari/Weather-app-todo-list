package com.dve.sari.coolweather.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dve.sari.coolweather.data.local.entity.WeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherEntity)

    @Query("SELECT * FROM weather ORDER BY timestamp DESC LIMIT 1")
    fun getLatestWeather(): Flow<WeatherEntity?>

    @Query("SELECT * FROM weather ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestWeatherOnce(): WeatherEntity?

    @Query("DELETE FROM weather")
    suspend fun deleteAllWeather()

    @Query("DELETE FROM weather WHERE timestamp < :timestamp")
    suspend fun deleteOldWeather(timestamp: Long)
}

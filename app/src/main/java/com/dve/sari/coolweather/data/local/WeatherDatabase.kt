package com.dve.sari.coolweather.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dve.sari.coolweather.data.local.dao.WeatherDao
import com.dve.sari.coolweather.data.local.dao.TaskDao
import com.dve.sari.coolweather.data.local.entity.WeatherEntity
import com.dve.sari.coolweather.data.local.entity.TaskEntity

@Database(
    entities = [WeatherEntity::class, TaskEntity::class],
    version = 3,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun weatherDao(): WeatherDao
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        fun getDatabase(context: Context): WeatherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java,
                    "weather_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

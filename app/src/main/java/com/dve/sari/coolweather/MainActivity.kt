package com.dve.sari.coolweather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dve.sari.coolweather.presentation.task.TaskScreen
import com.dve.sari.coolweather.presentation.task.TaskViewModel
import com.dve.sari.coolweather.presentation.task.TaskViewModelFactory
import com.dve.sari.coolweather.presentation.theme.ThemeViewModel
import com.dve.sari.coolweather.presentation.theme.ThemeViewModelFactory
import com.dve.sari.coolweather.presentation.weather.WeatherScreen
import com.dve.sari.coolweather.presentation.weather.WeatherViewModel
import com.dve.sari.coolweather.presentation.weather.WeatherViewModelFactory
import com.dve.sari.coolweather.ui.theme.CoolWeatherTheme
import com.dve.sari.coolweather.util.ThemeMode

class MainActivity : ComponentActivity() {

    private val apiKey = BuildConfig.WEATHER_API_KEY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeViewModel: ThemeViewModel = viewModel(
                factory = ThemeViewModelFactory(applicationContext)
            )
            val themeMode by themeViewModel.themeMode.collectAsState()
            val darkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            CoolWeatherTheme(darkTheme = darkTheme) {
                MainScreen(
                    apiKey = apiKey,
                    context = applicationContext,
                    themeViewModel = themeViewModel
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    apiKey: String,
    context: android.content.Context,
    themeViewModel: ThemeViewModel
) {
    var selectedTab by remember { mutableStateOf(0) }

    val weatherViewModel: WeatherViewModel = viewModel(
        factory = WeatherViewModelFactory(
            context = context,
            apiKey = apiKey
        )
    )

    val taskViewModel: TaskViewModel = viewModel(
        factory = TaskViewModelFactory(
            context = context
        )
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Weather") },
                    label = { Text("Weather") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onSurface,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = "Tasks") },
                    label = { Text("Tasks") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onSurface,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    ) { paddingValues ->
        when (selectedTab) {
            0 -> WeatherScreen(
                viewModel = weatherViewModel,
                themeViewModel = themeViewModel,
                modifier = Modifier.padding(paddingValues)
            )
            1 -> TaskScreen(
                viewModel = taskViewModel,
                weatherViewModel = weatherViewModel,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}
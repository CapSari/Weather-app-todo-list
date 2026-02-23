package com.dve.sari.coolweather.presentation.task

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dve.sari.coolweather.data.local.WeatherDatabase
import com.dve.sari.coolweather.data.repository.TaskRepositoryImpl
import com.dve.sari.coolweather.domain.usecase.AddTaskUseCase
import com.dve.sari.coolweather.domain.usecase.DeleteTaskUseCase
import com.dve.sari.coolweather.domain.usecase.GetTasksUseCase
import com.dve.sari.coolweather.domain.usecase.ToggleTaskCompletionUseCase

class TaskViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            val database = WeatherDatabase.getDatabase(context)
            val repository = TaskRepositoryImpl(database.taskDao())
            val getTasksUseCase = GetTasksUseCase(repository)
            val addTaskUseCase = AddTaskUseCase(repository)
            val toggleTaskCompletionUseCase = ToggleTaskCompletionUseCase(repository)
            val deleteTaskUseCase = DeleteTaskUseCase(repository)

            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(
                getTasksUseCase,
                addTaskUseCase,
                toggleTaskCompletionUseCase,
                deleteTaskUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

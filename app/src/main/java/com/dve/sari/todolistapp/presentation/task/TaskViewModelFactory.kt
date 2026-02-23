package com.dve.sari.todolistapp.presentation.task

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dve.sari.todolistapp.data.local.WeatherDatabase
import com.dve.sari.todolistapp.data.repository.TaskRepositoryImpl
import com.dve.sari.todolistapp.domain.usecase.AddTaskUseCase
import com.dve.sari.todolistapp.domain.usecase.DeleteTaskUseCase
import com.dve.sari.todolistapp.domain.usecase.GetTasksUseCase
import com.dve.sari.todolistapp.domain.usecase.ToggleTaskCompletionUseCase

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

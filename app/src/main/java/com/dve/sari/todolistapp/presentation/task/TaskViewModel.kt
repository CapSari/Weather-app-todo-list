package com.dve.sari.todolistapp.presentation.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dve.sari.todolistapp.domain.usecase.AddTaskUseCase
import com.dve.sari.todolistapp.domain.usecase.DeleteTaskUseCase
import com.dve.sari.todolistapp.domain.usecase.GetTasksUseCase
import com.dve.sari.todolistapp.domain.usecase.ToggleTaskCompletionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class TaskViewModel(
    private val getTasksUseCase: GetTasksUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val toggleTaskCompletionUseCase: ToggleTaskCompletionUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            combine(
                getTasksUseCase.getTodoTasks(),
                getTasksUseCase.getCompletedTasks()
            ) { todoTasks, completedTasks ->
                TaskUiState(
                    todoTasks = todoTasks,
                    completedTasks = completedTasks,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun addTask(title: String, description: String) {
        if (title.isBlank()) return

        viewModelScope.launch {
            try {
                addTaskUseCase(title.trim(), description.trim())
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to add task: ${e.message}"
                )
            }
        }
    }

    fun toggleTaskCompletion(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            try {
                toggleTaskCompletionUseCase(taskId, isCompleted)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to update task: ${e.message}"
                )
            }
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            try {
                deleteTaskUseCase(taskId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to delete task: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

package com.dve.sari.coolweather.presentation.task

import com.dve.sari.coolweather.domain.model.Task

data class TaskUiState(
    val todoTasks: List<Task> = emptyList(),
    val completedTasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

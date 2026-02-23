package com.dve.sari.todolistapp.presentation.task

import com.dve.sari.todolistapp.domain.model.Task

data class TaskUiState(
    val todoTasks: List<Task> = emptyList(),
    val completedTasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

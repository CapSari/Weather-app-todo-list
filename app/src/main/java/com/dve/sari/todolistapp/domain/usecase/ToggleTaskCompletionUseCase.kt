package com.dve.sari.todolistapp.domain.usecase

import com.dve.sari.todolistapp.domain.repository.TaskRepository

class ToggleTaskCompletionUseCase(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(taskId: Long, isCompleted: Boolean) {
        repository.toggleTaskCompletion(taskId, isCompleted)
    }
}

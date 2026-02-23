package com.dve.sari.coolweather.domain.usecase

import com.dve.sari.coolweather.domain.repository.TaskRepository

class ToggleTaskCompletionUseCase(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(taskId: Long, isCompleted: Boolean) {
        repository.toggleTaskCompletion(taskId, isCompleted)
    }
}

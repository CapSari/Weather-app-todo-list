package com.dve.sari.coolweather.domain.usecase

import com.dve.sari.coolweather.domain.repository.TaskRepository

class DeleteTaskUseCase(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(taskId: Long) {
        repository.deleteTaskById(taskId)
    }
}

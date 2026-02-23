package com.dve.sari.todolistapp.domain.usecase

import com.dve.sari.todolistapp.domain.repository.TaskRepository

class DeleteTaskUseCase(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(taskId: Long) {
        repository.deleteTaskById(taskId)
    }
}

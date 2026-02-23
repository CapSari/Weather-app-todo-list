package com.dve.sari.coolweather.domain.usecase

import com.dve.sari.coolweather.domain.model.Task
import com.dve.sari.coolweather.domain.repository.TaskRepository

class AddTaskUseCase(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(title: String, description: String): Long {
        val task = Task(
            title = title,
            description = description,
            isCompleted = false
        )
        return repository.addTask(task)
    }
}

package com.dve.sari.todolistapp.domain.usecase

import com.dve.sari.todolistapp.domain.model.Task
import com.dve.sari.todolistapp.domain.repository.TaskRepository

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

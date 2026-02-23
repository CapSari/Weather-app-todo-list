package com.dve.sari.todolistapp.domain.usecase

import com.dve.sari.todolistapp.domain.model.Task
import com.dve.sari.todolistapp.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetTasksUseCase(
    private val repository: TaskRepository
) {
    fun getTodoTasks(): Flow<List<Task>> = repository.getTodoTasks()

    fun getCompletedTasks(): Flow<List<Task>> = repository.getCompletedTasks()

    fun getAllTasks(): Flow<List<Task>> = repository.getAllTasks()
}

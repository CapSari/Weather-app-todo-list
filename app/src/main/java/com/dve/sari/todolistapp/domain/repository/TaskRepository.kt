package com.dve.sari.todolistapp.domain.repository

import com.dve.sari.todolistapp.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getTodoTasks(): Flow<List<Task>>
    fun getCompletedTasks(): Flow<List<Task>>
    fun getAllTasks(): Flow<List<Task>>
    suspend fun getTaskById(id: Long): Task?
    suspend fun addTask(task: Task): Long
    suspend fun updateTask(task: Task)
    suspend fun toggleTaskCompletion(id: Long, isCompleted: Boolean)
    suspend fun deleteTask(task: Task)
    suspend fun deleteTaskById(id: Long)
}

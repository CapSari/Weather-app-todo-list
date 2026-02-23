package com.dve.sari.todolistapp.presentation.task

import com.dve.sari.todolistapp.domain.model.Task
import com.dve.sari.todolistapp.domain.usecase.AddTaskUseCase
import com.dve.sari.todolistapp.domain.usecase.DeleteTaskUseCase
import com.dve.sari.todolistapp.domain.usecase.GetTasksUseCase
import com.dve.sari.todolistapp.domain.usecase.ToggleTaskCompletionUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModelTest {

    private lateinit var viewModel: TaskViewModel
    private lateinit var getTasksUseCase: GetTasksUseCase
    private lateinit var addTaskUseCase: AddTaskUseCase
    private lateinit var toggleTaskCompletionUseCase: ToggleTaskCompletionUseCase
    private lateinit var deleteTaskUseCase: DeleteTaskUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getTasksUseCase = mockk()
        addTaskUseCase = mockk()
        toggleTaskCompletionUseCase = mockk()
        deleteTaskUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() {
        viewModel = TaskViewModel(
            getTasksUseCase,
            addTaskUseCase,
            toggleTaskCompletionUseCase,
            deleteTaskUseCase
        )
    }

    @Test
    fun `viewModel loads todo and completed tasks on init`() = runTest {
        // Given
        val todoTasks = listOf(
            createMockTask(id = 1, title = "Task 1", isCompleted = false),
            createMockTask(id = 2, title = "Task 2", isCompleted = false)
        )
        val completedTasks = listOf(
            createMockTask(id = 3, title = "Task 3", isCompleted = true)
        )
        every { getTasksUseCase.getTodoTasks() } returns flowOf(todoTasks)
        every { getTasksUseCase.getCompletedTasks() } returns flowOf(completedTasks)

        // When
        createViewModel()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(2, state.todoTasks.size)
        assertEquals(1, state.completedTasks.size)
        assertEquals("Task 1", state.todoTasks[0].title)
        assertEquals("Task 3", state.completedTasks[0].title)
        assertFalse(state.isLoading)
    }

    @Test
    fun `viewModel loads empty lists when no tasks exist`() = runTest {
        // Given
        every { getTasksUseCase.getTodoTasks() } returns flowOf(emptyList())
        every { getTasksUseCase.getCompletedTasks() } returns flowOf(emptyList())

        // When
        createViewModel()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state.todoTasks.isEmpty())
        assertTrue(state.completedTasks.isEmpty())
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `addTask successfully adds a new task`() = runTest {
        // Given
        every { getTasksUseCase.getTodoTasks() } returns flowOf(emptyList())
        every { getTasksUseCase.getCompletedTasks() } returns flowOf(emptyList())
        coEvery { addTaskUseCase("New Task", "Description") } returns 1L

        // When
        createViewModel()
        advanceUntilIdle()
        viewModel.addTask("New Task", "Description")
        advanceUntilIdle()

        // Then
        coVerify { addTaskUseCase("New Task", "Description") }
    }

    @Test
    fun `addTask trims whitespace from title and description`() = runTest {
        // Given
        every { getTasksUseCase.getTodoTasks() } returns flowOf(emptyList())
        every { getTasksUseCase.getCompletedTasks() } returns flowOf(emptyList())
        coEvery { addTaskUseCase("Task", "Desc") } returns 1L

        // When
        createViewModel()
        advanceUntilIdle()
        viewModel.addTask("  Task  ", "  Desc  ")
        advanceUntilIdle()

        // Then
        coVerify { addTaskUseCase("Task", "Desc") }
    }

    @Test
    fun `addTask does not add task when title is blank`() = runTest {
        // Given
        every { getTasksUseCase.getTodoTasks() } returns flowOf(emptyList())
        every { getTasksUseCase.getCompletedTasks() } returns flowOf(emptyList())

        // When
        createViewModel()
        advanceUntilIdle()
        viewModel.addTask("   ", "Description")
        advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { addTaskUseCase(any(), any()) }
    }

    @Test
    fun `addTask does not add task when title is empty`() = runTest {
        // Given
        every { getTasksUseCase.getTodoTasks() } returns flowOf(emptyList())
        every { getTasksUseCase.getCompletedTasks() } returns flowOf(emptyList())

        // When
        createViewModel()
        advanceUntilIdle()
        viewModel.addTask("", "Description")
        advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { addTaskUseCase(any(), any()) }
    }

    @Test
    fun `addTask sets error when use case throws exception`() = runTest {
        // Given
        every { getTasksUseCase.getTodoTasks() } returns flowOf(emptyList())
        every { getTasksUseCase.getCompletedTasks() } returns flowOf(emptyList())
        coEvery { addTaskUseCase(any(), any()) } throws Exception("Database error")

        // When
        createViewModel()
        advanceUntilIdle()
        viewModel.addTask("Task", "Description")
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state.error?.contains("Failed to add task") == true)
        assertTrue(state.error?.contains("Database error") == true)
    }

    @Test
    fun `toggleTaskCompletion toggles task completion status`() = runTest {
        // Given
        every { getTasksUseCase.getTodoTasks() } returns flowOf(emptyList())
        every { getTasksUseCase.getCompletedTasks() } returns flowOf(emptyList())
        coEvery { toggleTaskCompletionUseCase(1L, true) } returns Unit

        // When
        createViewModel()
        advanceUntilIdle()
        viewModel.toggleTaskCompletion(1L, true)
        advanceUntilIdle()

        // Then
        coVerify { toggleTaskCompletionUseCase(1L, true) }
    }

    @Test
    fun `toggleTaskCompletion sets error when use case throws exception`() = runTest {
        // Given
        every { getTasksUseCase.getTodoTasks() } returns flowOf(emptyList())
        every { getTasksUseCase.getCompletedTasks() } returns flowOf(emptyList())
        coEvery { toggleTaskCompletionUseCase(any(), any()) } throws Exception("Update failed")

        // When
        createViewModel()
        advanceUntilIdle()
        viewModel.toggleTaskCompletion(1L, true)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state.error?.contains("Failed to update task") == true)
    }

    @Test
    fun `deleteTask successfully deletes task`() = runTest {
        // Given
        every { getTasksUseCase.getTodoTasks() } returns flowOf(emptyList())
        every { getTasksUseCase.getCompletedTasks() } returns flowOf(emptyList())
        coEvery { deleteTaskUseCase(1L) } returns Unit

        // When
        createViewModel()
        advanceUntilIdle()
        viewModel.deleteTask(1L)
        advanceUntilIdle()

        // Then
        coVerify { deleteTaskUseCase(1L) }
    }

    @Test
    fun `deleteTask sets error when use case throws exception`() = runTest {
        // Given
        every { getTasksUseCase.getTodoTasks() } returns flowOf(emptyList())
        every { getTasksUseCase.getCompletedTasks() } returns flowOf(emptyList())
        coEvery { deleteTaskUseCase(any()) } throws Exception("Delete failed")

        // When
        createViewModel()
        advanceUntilIdle()
        viewModel.deleteTask(1L)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state.error?.contains("Failed to delete task") == true)
    }

    @Test
    fun `clearError clears error state`() = runTest {
        // Given
        every { getTasksUseCase.getTodoTasks() } returns flowOf(emptyList())
        every { getTasksUseCase.getCompletedTasks() } returns flowOf(emptyList())
        coEvery { addTaskUseCase(any(), any()) } throws Exception("Error")

        // When
        createViewModel()
        advanceUntilIdle()
        viewModel.addTask("Task", "Desc")
        advanceUntilIdle()

        // Then - Error should be set
        assertTrue(viewModel.uiState.value.error != null)

        // When - Clear error
        viewModel.clearError()
        advanceUntilIdle()

        // Then - Error should be null
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `uiState emits updates when tasks change`() = runTest {
        // Given
        val initialTodoTasks = listOf(createMockTask(id = 1, title = "Task 1", isCompleted = false))
        val updatedTodoTasks = listOf(
            createMockTask(id = 1, title = "Task 1", isCompleted = false),
            createMockTask(id = 2, title = "Task 2", isCompleted = false)
        )
        every { getTasksUseCase.getTodoTasks() } returns flowOf(initialTodoTasks, updatedTodoTasks)
        every { getTasksUseCase.getCompletedTasks() } returns flowOf(emptyList())

        // When
        createViewModel()
        advanceUntilIdle()

        // Then
        val firstState = viewModel.uiState.value
        // After combining both flows, we get the latest state
        assertEquals(2, firstState.todoTasks.size)
    }

    @Test
    fun `viewModel handles task moving from todo to completed`() = runTest {
        // Given
        val task1 = createMockTask(id = 1, title = "Task 1", isCompleted = false)
        val task1Completed = createMockTask(id = 1, title = "Task 1", isCompleted = true)

        every { getTasksUseCase.getTodoTasks() } returns flowOf(listOf(task1), emptyList())
        every { getTasksUseCase.getCompletedTasks() } returns flowOf(emptyList(), listOf(task1Completed))

        // When
        createViewModel()
        advanceUntilIdle()

        // Then
        // The combine operator takes the latest values from both flows
        // So we get empty todo list and completed list with 1 task
        val state = viewModel.uiState.value
        assertEquals(0, state.todoTasks.size)
        assertEquals(1, state.completedTasks.size)
    }

    @Test
    fun `addTask with description allows empty description`() = runTest {
        // Given
        every { getTasksUseCase.getTodoTasks() } returns flowOf(emptyList())
        every { getTasksUseCase.getCompletedTasks() } returns flowOf(emptyList())
        coEvery { addTaskUseCase("Task", "") } returns 1L

        // When
        createViewModel()
        advanceUntilIdle()
        viewModel.addTask("Task", "")
        advanceUntilIdle()

        // Then
        coVerify { addTaskUseCase("Task", "") }
    }

    private fun createMockTask(
        id: Long = 1L,
        title: String = "Test Task",
        description: String = "Test Description",
        isCompleted: Boolean = false,
        createdAt: Long = System.currentTimeMillis(),
        completedAt: Long? = null
    ): Task {
        return Task(
            id = id,
            title = title,
            description = description,
            isCompleted = isCompleted,
            createdAt = createdAt,
            completedAt = completedAt
        )
    }
}

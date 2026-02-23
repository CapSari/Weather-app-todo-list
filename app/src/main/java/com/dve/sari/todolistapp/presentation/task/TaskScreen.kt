package com.dve.sari.todolistapp.presentation.task

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.setValue
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.dve.sari.todolistapp.domain.model.Task

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    viewModel: TaskViewModel,
    weatherViewModel: com.dve.sari.todolistapp.presentation.weather.WeatherViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val weatherUiState by weatherViewModel.uiState.collectAsState()
    var showAddTaskScreen by remember { mutableStateOf(false) }

    val gradientColors = when {
        weatherUiState is com.dve.sari.todolistapp.presentation.weather.WeatherUiState.Success -> {
            val weatherCondition = (weatherUiState as com.dve.sari.todolistapp.presentation.weather.WeatherUiState.Success).data.description.lowercase()
            val isLightMode = MaterialTheme.colorScheme.background.luminance() > 0.5f

            when {
                weatherCondition.contains("cloud") || weatherCondition.contains("overcast") -> {
                    if (isLightMode) {
                        listOf(
                            Color(0xFFB0BEC5),
                            Color(0xFFCFD8DC),
                            Color(0xFFECEFF1)
                        )
                    } else {
                        listOf(
                            Color(0xFF455A64),
                            Color(0xFF37474F),
                            Color(0xFF263238)
                        )
                    }
                }
                weatherCondition.contains("clear") || weatherCondition.contains("sun") -> {
                    if (isLightMode) {
                        listOf(
                            Color(0xFF90CAF9),
                            Color(0xFFBBDEFB),
                            Color(0xFFE3F2FD)
                        )
                    } else {
                        listOf(
                            Color(0xFF1565C0),
                            Color(0xFF0D47A1),
                            Color(0xFF01579B)
                        )
                    }
                }
                weatherCondition.contains("rain") || weatherCondition.contains("drizzle") -> {
                    if (isLightMode) {
                        listOf(
                            Color(0xFF90A4AE),
                            Color(0xFFB0BEC5),
                            Color(0xFFCFD8DC)
                        )
                    } else {
                        listOf(
                            Color(0xFF546E7A),
                            Color(0xFF455A64),
                            Color(0xFF37474F)
                        )
                    }
                }
                else -> {
                    if (isLightMode) {
                        listOf(
                            Color(0xFFE0E0E0),
                            Color(0xFFF5F5F5),
                            Color(0xFFFFFFFF)
                        )
                    } else {
                        listOf(
                            Color(0xFF1E1E1E),
                            Color(0xFF121212),
                            Color(0xFF0A0A0A)
                        )
                    }
                }
            }
        }
        else -> {
            if (MaterialTheme.colorScheme.background.luminance() > 0.5f) {
                listOf(
                    Color(0xFFE0E0E0),
                    Color(0xFFF5F5F5),
                    Color(0xFFFFFFFF)
                )
            } else {
                listOf(
                    Color(0xFF1E1E1E),
                    Color(0xFF121212),
                    Color(0xFF0A0A0A)
                )
            }
        }
    }

    if (showAddTaskScreen) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(colors = gradientColors)
                )
        ) {
            AddTaskScreen(
                onDismiss = { showAddTaskScreen = false },
                onSaveTask = { title, description ->
                    viewModel.addTask(title, description)
                    showAddTaskScreen = false
                },
                gradientColors = gradientColors
            )
        }
    } else {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(colors = gradientColors)
                )
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Text(
                                text = "To do",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            if (uiState.todoTasks.isEmpty()) {
                                Text(
                                    text = "No tasks yet",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            } else {
                                uiState.todoTasks.forEachIndexed { index, task ->
                                    GoogleFormTaskItem(
                                        task = task,
                                        onToggleCompletion = { viewModel.toggleTaskCompletion(task.id, !task.isCompleted) },
                                        onDelete = { viewModel.deleteTask(task.id) }
                                    )
                                    if (index < uiState.todoTasks.size - 1) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Text(
                                text = "Completed",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            if (uiState.completedTasks.isEmpty()) {
                                Text(
                                    text = "No completed tasks",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            } else {
                                uiState.completedTasks.forEachIndexed { index, task ->
                                    GoogleFormTaskItem(
                                        task = task,
                                        onToggleCompletion = { viewModel.toggleTaskCompletion(task.id, !task.isCompleted) },
                                        onDelete = { viewModel.deleteTask(task.id) }
                                    )
                                    if (index < uiState.completedTasks.size - 1) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = { showAddTaskScreen = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }

            uiState.error?.let { error ->
                LaunchedEffect(error) {
                    viewModel.clearError()
                }
            }
        }
    }
}

@Composable
private fun GoogleFormTaskItem(
    task: Task,
    onToggleCompletion: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = { onToggleCompletion() },
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Normal,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                color = if (task.isCompleted)
                    MaterialTheme.colorScheme.onSurfaceVariant
                else
                    MaterialTheme.colorScheme.onSurface
            )

            if (task.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete task",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    onDismiss: () -> Unit,
    onSaveTask: (String, String) -> Unit,
    gradientColors: List<Color> = if (MaterialTheme.colorScheme.background.luminance() > 0.5f) {
        listOf(
            Color(0xFFE0E0E0),
            Color(0xFFF5F5F5),
            Color(0xFFFFFFFF)
        )
    } else {
        listOf(
            Color(0xFF1E1E1E),
            Color(0xFF121212),
            Color(0xFF0A0A0A)
        )
    }
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("New Task", fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (title.isNotBlank()) {
                                onSaveTask(title, description)
                            }
                        },
                        enabled = title.isNotBlank()
                    ) {
                        Text("Save", fontWeight = FontWeight.Medium)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Task title",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text("What needs to be done?") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            focusedBorderColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Description (optional)",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { Text("Add more details...") },
                        minLines = 5,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            focusedBorderColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    }
}

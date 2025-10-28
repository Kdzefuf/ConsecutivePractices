package com.example.consecutivepractices.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.consecutivepractices.viewmodel.FilterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(
    navController: NavController,
    viewModel: FilterViewModel = hiltViewModel()
) {
    val filterData by viewModel.filterData.collectAsState(initial = com.example.consecutivepractices.data.preferences.FilterData())

    var genre by remember { mutableStateOf("") }
    var minRating by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }

    // Инициализируем поля при получении данных
    LaunchedEffect(filterData) {
        genre = filterData.genre
        minRating = filterData.minRating
        year = filterData.year
    }

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        genre = ""
                        minRating = ""
                        year = ""
                        viewModel.clearFilters()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Сбросить")
                }
                Button(
                    onClick = {
                        viewModel.saveFilters(genre, minRating, year)
                        // Закрываем экран после сохранения
                        navController.popBackStack()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Применить")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Настройте фильтры для поиска фильмов",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Жанр
            OutlinedTextField(
                value = genre,
                onValueChange = { genre = it },
                label = { Text("Жанр (например: комедия, драма)") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    if (genre.isNotBlank()) {
                        IconButton(onClick = { genre = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Очистить")
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Минимальный рейтинг
            OutlinedTextField(
                value = minRating,
                onValueChange = { newValue ->
                    // Разрешаем только цифры и точку
                    if (newValue.isEmpty() || newValue.matches(Regex("^[0-9]*(\\.[0-9]{0,1})?\$")) && newValue.length <= 4) {
                        if (newValue.isEmpty() || newValue.toDoubleOrNull()?.let { it >= 0.0 && it <= 10.0 } == true) {
                            minRating = newValue
                        }
                    }
                },
                label = { Text("Минимальный рейтинг (0-10)") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    if (minRating.isNotBlank()) {
                        IconButton(onClick = { minRating = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Очистить")
                        }
                    }
                },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default.copy(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Год
            // Год - МИНИМАЛЬНАЯ ВАЛИДАЦИЯ
            OutlinedTextField(
                value = year,
                onValueChange = { newValue ->
                    // Разрешаем любой ввод, но ограничиваем длину
                    if (newValue.length <= 4) {
                        year = newValue
                    }
                },
                label = { Text("Год выпуска (1900-2030)") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    if (year.isNotBlank()) {
                        IconButton(onClick = { year = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Очистить")
                        }
                    }
                },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Подсказки
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Подсказки:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "• Жанр: комедия, драма, боевик, фантастика",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "• Рейтинг: от 0 до 10 (например: 7.5)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "• Год: от 1900 до 2030",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
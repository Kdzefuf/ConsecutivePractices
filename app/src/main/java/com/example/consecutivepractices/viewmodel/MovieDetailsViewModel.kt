package com.example.consecutivepractices.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.consecutivepractices.domain.models.Movie
import com.example.consecutivepractices.domain.usecase.GetMovieDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase
) : ViewModel() {

    private val _movie = MutableStateFlow<Movie?>(null)
    val movie: StateFlow<Movie?> = _movie.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadMovieDetails()
    }

    private fun loadMovieDetails() {
        val movieId = getMovieIdFromSavedState()
        movieId?.let { id ->
            viewModelScope.launch {
                _isLoading.value = true
                _error.value = null

                try {
                    val result = getMovieDetailsUseCase(movieId = id)

                    _isLoading.value = false

                    result.onSuccess { movie ->
                        _movie.value = movie
                    }.onFailure { exception ->
                        val errorMessage = when {
                            exception.message?.contains("name") == true ->
                                "Ошибка данных фильма. Некоторые данные отсутствуют."
                            exception.message?.contains("404") == true ->
                                "Фильм не найден"
                            exception.message?.contains("401") == true ->
                                "Ошибка авторизации API"
                            else -> "Ошибка загрузки деталей фильма: ${exception.message}"
                        }
                        _error.value = errorMessage
                    }
                } catch (e: Exception) {
                    _isLoading.value = false
                    _error.value = "Неожиданная ошибка: ${e.message}"
                }
            }
        } ?: run {
            _error.value = "ID фильма не найден"
        }
    }

    private fun getMovieIdFromSavedState(): Int? {
        return try {
            savedStateHandle.get<Int>("movieId")
        } catch (e: Exception) {
            savedStateHandle.get<String>("movieId")?.toIntOrNull()
        }
    }

    fun shareMovie(context: Context) {
        _movie.value?.let { movie ->
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Посмотрите этот фильм!")
                putExtra(Intent.EXTRA_TEXT, "Фильм: ${movie.title} (${movie.year})\nРейтинг: ${movie.rating}\nЖанр: ${movie.genre}\nРежиссер: ${movie.director}\nОписание: ${movie.synopsis}\nПоделитесь этим потрясающим фильмом!")
            }
            context.startActivity(Intent.createChooser(shareIntent, "Поделиться"))
        }
    }

    fun retry() {
        loadMovieDetails()
    }
}
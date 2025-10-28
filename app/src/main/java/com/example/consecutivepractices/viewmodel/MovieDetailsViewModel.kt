package com.example.consecutivepractices.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.consecutivepractices.domain.models.Movie
import com.example.consecutivepractices.domain.repository.FavoriteRepository
import com.example.consecutivepractices.domain.repository.MovieRepository
import com.example.consecutivepractices.domain.usecase.AddToFavoritesUseCase
import com.example.consecutivepractices.domain.usecase.GetMovieDetailsUseCase
import com.example.consecutivepractices.domain.usecase.IsFavoriteUseCase
import com.example.consecutivepractices.domain.usecase.RemoveFromFavoritesUseCase
import com.example.consecutivepractices.ui.state.MovieDetailsState
import com.example.consecutivepractices.util.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val movieRepository: MovieRepository,
    private val favoriteRepository: FavoriteRepository,
    private val errorHandler: ErrorHandler
) : ViewModel() {

    private val getMovieDetailsUseCase = GetMovieDetailsUseCase(movieRepository)
    private val addToFavoritesUseCase = AddToFavoritesUseCase(favoriteRepository)
    private val removeFromFavoritesUseCase = RemoveFromFavoritesUseCase(favoriteRepository)
    private val isFavoriteUseCase = IsFavoriteUseCase(favoriteRepository)

    private val _state = MutableStateFlow<MovieDetailsState>(MovieDetailsState.Loading)
    val state: StateFlow<MovieDetailsState> = _state.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    init {
        loadMovieDetails()
        checkIfFavorite()
    }

    private fun loadMovieDetails() {
        val movieId = getMovieIdFromSavedState()
        movieId?.let { id ->
            viewModelScope.launch {
                _state.value = MovieDetailsState.Loading

                val result = getMovieDetailsUseCase(movieId = id)

                result.onSuccess { movie ->
                    _state.value = MovieDetailsState.Success(movie)
                }.onFailure { exception ->
                    _state.value = MovieDetailsState.Error(errorHandler.getErrorMessage(exception as Exception))
                }
            }
        } ?: run {
            _state.value = MovieDetailsState.Error("ID фильма не найден")
        }
    }

    private fun checkIfFavorite() {
        val movieId = getMovieIdFromSavedState()
        movieId?.let { id ->
            viewModelScope.launch {
                _isFavorite.value = isFavoriteUseCase(id)
            }
        }
    }

    private fun getMovieIdFromSavedState(): Int? {
        return try {
            savedStateHandle.get<Int>("movieId")
        } catch (e: Exception) {
            savedStateHandle.get<String>("movieId")?.toIntOrNull()
        }
    }

    fun toggleFavorite() {
        val movie = (_state.value as? MovieDetailsState.Success)?.movie
        movie?.let {
            viewModelScope.launch {
                if (_isFavorite.value) {
                    removeFromFavoritesUseCase(movie.id)
                    _isFavorite.value = false
                } else {
                    addToFavoritesUseCase(movie)
                    _isFavorite.value = true
                }
            }
        }
    }

    fun shareMovie(context: Context) {
        val movie = (_state.value as? MovieDetailsState.Success)?.movie
        movie?.let {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Посмотрите этот фильм!")
                putExtra(Intent.EXTRA_TEXT, "Фильм: ${it.title} (${it.year})\nРейтинг: ${it.rating}\nЖанр: ${it.genre}\nРежиссер: ${it.director}\nОписание: ${it.synopsis}\nПоделитесь этим потрясающим фильмом!")
            }
            context.startActivity(Intent.createChooser(shareIntent, "Поделиться"))
        }
    }

    fun retry() {
        loadMovieDetails()
        checkIfFavorite()
    }
}
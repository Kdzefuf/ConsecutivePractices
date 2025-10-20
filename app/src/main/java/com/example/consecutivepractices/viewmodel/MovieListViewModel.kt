package com.example.consecutivepractices.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.consecutivepractices.domain.models.Movie
import com.example.consecutivepractices.domain.usecase.GetPopularMoviesUseCase
import com.example.consecutivepractices.domain.usecase.SearchMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase,
    private val searchMoviesUseCase: SearchMoviesUseCase
) : ViewModel() {

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var currentPage = 1
    private var canLoadMore = true

    init {
        loadPopularMovies()
    }

    fun loadPopularMovies(
        page: Int = 1,
        year: String? = null,
        minRating: String? = "7",
        genre: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = getPopularMoviesUseCase(
                page = page,
                year = year,
                minRating = minRating,
                genre = genre
            )

            _isLoading.value = false

            result.onSuccess { movies ->
                if (page == 1) {
                    _movies.value = movies
                } else {
                    _movies.value = _movies.value + movies
                }
                canLoadMore = movies.isNotEmpty()
                currentPage = page
            }.onFailure { exception ->
                _error.value = "Ошибка загрузки: ${exception.message}"
            }
        }
    }

    fun searchMovies(query: String) {
        if (query.isBlank()) {
            loadPopularMovies()
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = searchMoviesUseCase(query = query)

            _isLoading.value = false

            result.onSuccess { movies ->
                _movies.value = movies
            }.onFailure { exception ->
                _error.value = "Ошибка поиска: ${exception.message}"
            }
        }
    }

    fun loadNextPage() {
        if (!_isLoading.value && canLoadMore) {
            loadPopularMovies(page = currentPage + 1)
        }
    }

    fun clearError() {
        _error.value = null
    }
}
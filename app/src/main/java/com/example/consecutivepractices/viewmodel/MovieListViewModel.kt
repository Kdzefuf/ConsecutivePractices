package com.example.consecutivepractices.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.consecutivepractices.domain.usecase.GetPopularMoviesUseCase
import com.example.consecutivepractices.domain.usecase.SearchMoviesUseCase
import com.example.consecutivepractices.ui.state.MovieListState
import com.example.consecutivepractices.util.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase,
    private val searchMoviesUseCase: SearchMoviesUseCase,
    private val errorHandler: ErrorHandler
) : ViewModel() {

    private val _state = MutableStateFlow<MovieListState>(MovieListState.Loading)
    val state: StateFlow<MovieListState> = _state.asStateFlow()

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
        viewModelScope.launch(errorHandler.coroutineExceptionHandler) {
            _state.value = MovieListState.Loading

            val result = getPopularMoviesUseCase(
                page = page,
                year = year,
                minRating = minRating,
                genre = genre
            )

            result.onSuccess { movies ->
                val currentState = _state.value as? MovieListState.Success
                val currentMovies = if (page == 1) movies else currentState?.movies?.plus(movies) ?: movies

                _state.value = MovieListState.Success(
                    movies = currentMovies,
                    canLoadMore = movies.isNotEmpty(),
                    isSearching = currentState?.isSearching ?: false,
                    searchQuery = currentState?.searchQuery ?: ""
                )
                canLoadMore = movies.isNotEmpty()
                currentPage = page
            }.onFailure { exception ->
                _state.value = MovieListState.Error(errorHandler.getErrorMessage(exception as Exception))
            }
        }
    }

    fun searchMovies(query: String) {
        if (query.isBlank()) {
            loadPopularMovies()
            return
        }

        viewModelScope.launch(errorHandler.coroutineExceptionHandler) {
            _state.value = MovieListState.Loading

            val result = searchMoviesUseCase(query = query)

            result.onSuccess { movies ->
                _state.value = MovieListState.Success(
                    movies = movies,
                    canLoadMore = false,
                    isSearching = true,
                    searchQuery = query
                )
            }.onFailure { exception ->
                _state.value = MovieListState.Error(errorHandler.getErrorMessage(exception as Exception))
            }
        }
    }

    fun setSearchState(isSearching: Boolean, query: String = "") {
        val currentState = _state.value as? MovieListState.Success
        _state.value = MovieListState.Success(
            movies = currentState?.movies ?: emptyList(),
            canLoadMore = currentState?.canLoadMore ?: true,
            isSearching = isSearching,
            searchQuery = query
        )
    }

    fun loadNextPage() {
        val currentState = _state.value as? MovieListState.Success
        if (currentState?.canLoadMore == true && !currentState.isSearching) {
            loadPopularMovies(page = currentPage + 1)
        }
    }

    fun clearError() {
        val currentState = _state.value as? MovieListState.Success
        _state.value = currentState ?: MovieListState.Success(
            movies = emptyList(),
            canLoadMore = true,
            isSearching = false
        )
    }
}
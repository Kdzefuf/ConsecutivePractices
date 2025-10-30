package com.example.consecutivepractices.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.consecutivepractices.data.preferences.FilterData
import com.example.consecutivepractices.data.preferences.FilterPreferences
import com.example.consecutivepractices.domain.repository.MovieRepository
import com.example.consecutivepractices.domain.usecase.GetPopularMoviesUseCase
import com.example.consecutivepractices.domain.usecase.SearchMoviesUseCase
import com.example.consecutivepractices.ui.state.MovieListState
import com.example.consecutivepractices.util.ErrorHandler
import com.example.consecutivepractices.util.FilterBadgeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val errorHandler: ErrorHandler,
    private val filterPreferences: FilterPreferences,
    private val filterBadgeManager: FilterBadgeManager
) : ViewModel() {

    private val getPopularMoviesUseCase = GetPopularMoviesUseCase(movieRepository)
    private val searchMoviesUseCase = SearchMoviesUseCase(movieRepository)

    private val _state = MutableStateFlow<MovieListState>(MovieListState.Loading)
    val state: StateFlow<MovieListState> = _state.asStateFlow()

    private val _filterData = MutableStateFlow<FilterData>(FilterData())
    val filterData: StateFlow<FilterData> = _filterData.asStateFlow()

    private var currentPage = 1
    private var canLoadMore = true
    private var isLoadingMore = false

    init {
        loadFilterData()
    }

    private fun loadFilterData() {
        viewModelScope.launch {
            filterPreferences.filterData.collect { filterData ->
                _filterData.value = filterData
                filterBadgeManager.setBadgeVisibility(filterData.hasActiveFilters)
                loadPopularMovies(useFilters = true)
            }
        }
    }

    fun loadPopularMovies(
        page: Int = 1,
        useFilters: Boolean = true
    ) {
        viewModelScope.launch {
            // Только для первой загрузки показываем полный Loading
            if (page == 1) {
                _state.value = MovieListState.Loading
            } else {
                // Для последующих страниц сохраняем текущие фильмы
                isLoadingMore = true
            }

            val currentFilters = _filterData.value
            val year = if (useFilters) currentFilters.year.takeIf { it.isNotBlank() } else null
            val minRating = if (useFilters) currentFilters.minRating.takeIf { it.isNotBlank() } else null
            val genre = if (useFilters) currentFilters.genre.takeIf { it.isNotBlank() } else null

            val result = getPopularMoviesUseCase(
                page = page,
                year = year,
                minRating = minRating,
                genre = genre
            )

            result.onSuccess { movies ->
                val currentState = _state.value
                val currentMovies = when {
                    page == 1 -> movies
                    currentState is MovieListState.Success -> currentState.movies + movies
                    else -> movies
                }

                _state.value = MovieListState.Success(
                    movies = currentMovies,
                    canLoadMore = movies.isNotEmpty(),
                    isSearching = (currentState as? MovieListState.Success)?.isSearching ?: false,
                    searchQuery = (currentState as? MovieListState.Success)?.searchQuery ?: "",
                    isLoadingMore = false
                )
                canLoadMore = movies.isNotEmpty()
                currentPage = page
                isLoadingMore = false
            }.onFailure { exception ->
                // При ошибке сохраняем текущие фильмы
                val currentState = _state.value as? MovieListState.Success
                _state.value = if (currentState != null) {
                    currentState.copy(isLoadingMore = false)
                } else {
                    MovieListState.Error(errorHandler.getErrorMessage(exception as Exception))
                }
                isLoadingMore = false
            }
        }
    }

    fun searchMovies(query: String) {
        if (query.isBlank()) {
            loadPopularMovies()
            return
        }

        viewModelScope.launch {
            _state.value = MovieListState.Loading

            val result = searchMoviesUseCase(query = query)

            result.onSuccess { movies ->
                _state.value = MovieListState.Success(
                    movies = movies,
                    canLoadMore = false,
                    isSearching = true,
                    searchQuery = query,
                    isLoadingMore = false
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
            searchQuery = query,
            isLoadingMore = false
        )
    }

    fun loadNextPage() {
        val currentState = _state.value as? MovieListState.Success
        if (currentState?.canLoadMore == true && !currentState.isSearching && !isLoadingMore) {
            loadPopularMovies(page = currentPage + 1)
        }
    }

    fun clearError() {
        val currentState = _state.value as? MovieListState.Success
        _state.value = currentState ?: MovieListState.Success(
            movies = emptyList(),
            canLoadMore = true,
            isSearching = false,
            searchQuery = "",
            isLoadingMore = false
        )
    }

    fun clearFilters() {
        viewModelScope.launch {
            filterPreferences.clearFilters()
        }
    }
}
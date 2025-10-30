package com.example.consecutivepractices.ui.state

import com.example.consecutivepractices.domain.models.Movie

sealed class MovieListState {
    object Loading : MovieListState()
    data class Success(
        val movies: List<Movie>,
        val canLoadMore: Boolean = true,
        val isSearching: Boolean = false,
        val searchQuery: String = "",
        val isLoadingMore: Boolean = false // Добавляем флаг загрузки дополнительных данных
    ) : MovieListState()
    data class Error(val message: String) : MovieListState()
}
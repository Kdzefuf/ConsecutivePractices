package com.example.consecutivepractices.ui.state

import com.example.consecutivepractices.domain.models.Movie

sealed class MovieDetailsState {
    object Loading : MovieDetailsState()
    data class Success(val movie: Movie) : MovieDetailsState()
    data class Error(val message: String) : MovieDetailsState()
}
package com.example.consecutivepractices.domain.usecase

import com.example.consecutivepractices.domain.models.Movie
import com.example.consecutivepractices.domain.repository.FavoriteRepository

class AddToFavoritesUseCase(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(movie: Movie) {
        repository.addToFavorites(movie)
    }
}
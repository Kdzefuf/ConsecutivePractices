package com.example.consecutivepractices.domain.usecase

import com.example.consecutivepractices.domain.repository.FavoriteRepository

class RemoveFromFavoritesUseCase(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(movieId: Int) {
        repository.removeFromFavorites(movieId)
    }
}
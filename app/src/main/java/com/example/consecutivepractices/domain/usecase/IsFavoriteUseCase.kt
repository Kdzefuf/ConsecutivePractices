package com.example.consecutivepractices.domain.usecase

import com.example.consecutivepractices.domain.repository.FavoriteRepository

class IsFavoriteUseCase(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(movieId: Int): Boolean {
        return repository.isFavorite(movieId)
    }
}
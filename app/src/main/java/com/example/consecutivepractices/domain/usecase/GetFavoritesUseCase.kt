package com.example.consecutivepractices.domain.usecase

import com.example.consecutivepractices.domain.models.Movie
import com.example.consecutivepractices.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow

class GetFavoritesUseCase(
    private val repository: FavoriteRepository
) {
    operator fun invoke(): Flow<List<Movie>> {
        return repository.getAllFavorites()
    }
}
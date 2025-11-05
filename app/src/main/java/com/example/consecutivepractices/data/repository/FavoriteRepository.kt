package com.example.consecutivepractices.domain.repository

import com.example.consecutivepractices.domain.models.Movie
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getAllFavorites(): Flow<List<Movie>>
    suspend fun addToFavorites(movie: Movie)
    suspend fun removeFromFavorites(movieId: Int)
    suspend fun isFavorite(movieId: Int): Boolean
}
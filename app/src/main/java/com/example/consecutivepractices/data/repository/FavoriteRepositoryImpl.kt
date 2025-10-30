package com.example.consecutivepractices.data.repository

import com.example.consecutivepractices.data.preferences.FavoritesPreferences
import com.example.consecutivepractices.domain.models.Movie
import com.example.consecutivepractices.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepositoryImpl @Inject constructor(
    private val favoritesPreferences: FavoritesPreferences
) : FavoriteRepository {

    override fun getAllFavorites(): Flow<List<Movie>> {
        return favoritesPreferences.favorites
    }

    override suspend fun addToFavorites(movie: Movie) {
        favoritesPreferences.addToFavorites(movie)
    }

    override suspend fun removeFromFavorites(movieId: Int) {
        favoritesPreferences.removeFromFavorites(movieId)
    }

    override suspend fun isFavorite(movieId: Int): Boolean {
        return favoritesPreferences.isFavorite(movieId)
    }
}
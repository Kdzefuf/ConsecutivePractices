package com.example.consecutivepractices.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.consecutivepractices.domain.models.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private val Context.favoritesDataStore: DataStore<Preferences> by preferencesDataStore(name = "favorites_preferences")

@Singleton
class FavoritesPreferences @Inject constructor(
    private val context: Context
) {
    companion object {
        private val FAVORITES_KEY = stringSetPreferencesKey("favorite_movies")
    }

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
    }

    val favorites: Flow<List<Movie>> = context.favoritesDataStore.data.map { preferences ->
        val favoriteStrings = preferences[FAVORITES_KEY] ?: emptySet()
        favoriteStrings.mapNotNull { jsonString ->
            try {
                json.decodeFromString<Movie>(jsonString)
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun addToFavorites(movie: Movie) {
        context.favoritesDataStore.edit { preferences ->
            val currentFavorites = preferences[FAVORITES_KEY] ?: emptySet()
            val movieJson = json.encodeToString(movie)
            preferences[FAVORITES_KEY] = currentFavorites + movieJson
        }
    }

    suspend fun removeFromFavorites(movieId: Int) {
        context.favoritesDataStore.edit { preferences ->
            val currentFavorites = preferences[FAVORITES_KEY] ?: emptySet()
            val updatedFavorites = currentFavorites.filterNot { jsonString ->
                try {
                    val movie = json.decodeFromString<Movie>(jsonString)
                    movie.id == movieId
                } catch (e: Exception) {
                    false
                }
            }.toSet()
            preferences[FAVORITES_KEY] = updatedFavorites
        }
    }

    suspend fun isFavorite(movieId: Int): Boolean {
        val currentFavorites = context.favoritesDataStore.data.map { preferences ->
            preferences[FAVORITES_KEY] ?: emptySet()
        }.first()

        return currentFavorites.any { jsonString ->
            try {
                val movie = json.decodeFromString<Movie>(jsonString)
                movie.id == movieId
            } catch (e: Exception) {
                false
            }
        }
    }
}
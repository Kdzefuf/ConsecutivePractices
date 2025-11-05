package com.example.consecutivepractices.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "filter_preferences")

@Singleton
class FilterPreferences @Inject constructor(
    private val context: Context
) {
    companion object {
        private val GENRE_KEY = stringPreferencesKey("genre")
        private val MIN_RATING_KEY = stringPreferencesKey("min_rating")
        private val YEAR_KEY = stringPreferencesKey("year")
        private val HAS_ACTIVE_FILTERS_KEY = booleanPreferencesKey("has_active_filters")
    }

    val filterData: Flow<FilterData> = context.dataStore.data.map { preferences ->
        FilterData(
            genre = preferences[GENRE_KEY] ?: "",
            minRating = preferences[MIN_RATING_KEY] ?: "",
            year = preferences[YEAR_KEY] ?: "",
            hasActiveFilters = preferences[HAS_ACTIVE_FILTERS_KEY] ?: false
        )
    }

    suspend fun saveFilters(genre: String, minRating: String, year: String) {
        context.dataStore.edit { preferences ->
            preferences[GENRE_KEY] = genre
            preferences[MIN_RATING_KEY] = minRating
            preferences[YEAR_KEY] = year
            preferences[HAS_ACTIVE_FILTERS_KEY] = genre.isNotBlank() || minRating.isNotBlank() || year.isNotBlank()
        }
    }

    suspend fun clearFilters() {
        context.dataStore.edit { preferences ->
            preferences.remove(GENRE_KEY)
            preferences.remove(MIN_RATING_KEY)
            preferences.remove(YEAR_KEY)
            preferences[HAS_ACTIVE_FILTERS_KEY] = false
        }
    }
}

data class FilterData(
    val genre: String = "",
    val minRating: String = "",
    val year: String = "",
    val hasActiveFilters: Boolean = false
)
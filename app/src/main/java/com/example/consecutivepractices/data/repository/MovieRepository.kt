package com.example.consecutivepractices.domain.repository

import com.example.consecutivepractices.domain.models.Movie

interface MovieRepository {
    suspend fun getPopularMovies(
        page: Int = 1,
        limit: Int = 10,
        year: String? = null,
        minRating: String? = null,
        genre: String? = null
    ): Result<List<Movie>>

    suspend fun searchMovies(
        query: String,
        page: Int = 1,
        limit: Int = 10
    ): Result<List<Movie>>

    suspend fun getMovieDetails(movieId: Int): Result<Movie>
}
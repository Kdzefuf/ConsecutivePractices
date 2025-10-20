package com.example.consecutivepractices.data.repository

import com.example.consecutivepractices.data.remote.KinopoiskApi
import com.example.consecutivepractices.domain.models.Movie
import com.example.consecutivepractices.domain.repository.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val api: KinopoiskApi
) : MovieRepository {

    override suspend fun getPopularMovies(
        page: Int,
        limit: Int,
        year: String?,
        minRating: String?,
        genre: String?
    ): Result<List<Movie>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getMovies(
                page = page,
                limit = limit,
                year = year,
                minRating = minRating,
                genre = genre
            )
            Result.success(response.docs.map { it.toMovie() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchMovies(query: String, page: Int, limit: Int): Result<List<Movie>> = withContext(Dispatchers.IO) {
        try {
            val response = api.searchMovies(
                page = page,
                limit = limit,
                query = query
            )
            Result.success(response.docs.map { it.toMovie() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMovieDetails(movieId: Int): Result<Movie> = withContext(Dispatchers.IO) {
        try {
            val response = api.getMovieById(id = movieId)
            Result.success(response.toMovie())
        } catch (e: Exception) {
            Result.failure(Exception("Ошибка загрузки деталей фильма: ${e.message}"))
        }
    }
}
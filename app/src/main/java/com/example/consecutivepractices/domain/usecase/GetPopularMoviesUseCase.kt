package com.example.consecutivepractices.domain.usecase

import com.example.consecutivepractices.domain.models.Movie
import com.example.consecutivepractices.domain.repository.MovieRepository
import javax.inject.Inject

class GetPopularMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(
        page: Int = 1,
        limit: Int = 20,
        year: String? = null,
        minRating: String? = "7",
        genre: String? = null
    ): Result<List<Movie>> {
        return repository.getPopularMovies(page, limit, year, minRating, genre)
    }
}
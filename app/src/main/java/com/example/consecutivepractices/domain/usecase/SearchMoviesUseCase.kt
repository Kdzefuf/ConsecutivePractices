package com.example.consecutivepractices.domain.usecase

import com.example.consecutivepractices.domain.models.Movie
import com.example.consecutivepractices.domain.repository.MovieRepository
import javax.inject.Inject

class SearchMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(
        query: String,
        page: Int = 1,
        limit: Int = 20
    ): Result<List<Movie>> {
        return repository.searchMovies(query, page, limit)
    }
}
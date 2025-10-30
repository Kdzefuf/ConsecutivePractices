package com.example.consecutivepractices.domain.usecase

import com.example.consecutivepractices.domain.models.Movie
import com.example.consecutivepractices.domain.repository.MovieRepository

class GetPopularMoviesUseCase(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(
        page: Int = 1,
        limit: Int = 20,
        year: String? = null,
        minRating: String? = null,
        genre: String? = null
    ): Result<List<Movie>> {
        // Просто передаем параметры в репозиторий
        // Валидация уже выполнена в UI и ViewModel
        return repository.getPopularMovies(
            page = page,
            limit = limit,
            year = year,
            minRating = minRating,
            genre = genre
        )
    }
}
package com.example.consecutivepractices.domain.usecase

import com.example.consecutivepractices.domain.models.Movie
import com.example.consecutivepractices.domain.repository.MovieRepository
import javax.inject.Inject

class GetMovieDetailsUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(movieId: Int): Result<Movie> {
        return repository.getMovieDetails(movieId)
    }
}
package com.example.consecutivepractices.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.consecutivepractices.data.Movie

class MovieViewModel : ViewModel() {
    private val _movies = mutableStateListOf(
        Movie(1, "Inception", 2010, 8.8, "Sci-Fi", "Christopher Nolan", "A mind-bending heist..."),
        Movie(2, "The Dark Knight", 2008, 9.0, "Action", "Christopher Nolan", "A battle with the Joker..."),
        Movie(3, "Interstellar", 2014, 8.6, "Sci-Fi", "Christopher Nolan", "A journey through space...")
    )

    val movies: List<Movie> get() = _movies

    fun getMovieById(id: Int): Movie? = _movies.find { it.id == id }
}
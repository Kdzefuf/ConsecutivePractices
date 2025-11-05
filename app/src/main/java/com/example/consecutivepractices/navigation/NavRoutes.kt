package com.example.consecutivepractices.navigation

object NavRoutes {
    const val MOVIE_LIST = "movie_list"
    const val MOVIE_DETAILS = "movie_details/{movieId}"
    const val FILTERS = "filters"
    const val FAVORITES = "favorites"

    fun movieDetails(movieId: Int): String {
        return "movie_details/$movieId"
    }
}
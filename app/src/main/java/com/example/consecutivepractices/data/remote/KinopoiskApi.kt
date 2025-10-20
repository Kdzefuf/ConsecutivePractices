package com.example.consecutivepractices.data.remote

import com.example.consecutivepractices.data.models.MovieDto
import com.example.consecutivepractices.data.models.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface KinopoiskApi {
    @GET("v1.4/movie")
    suspend fun getMovies(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10,
        @Query("year") year: String? = null,
        @Query("rating.kp") minRating: String? = null,
        @Query("genres.name") genre: String? = null,
        @Query("sortField") sortField: String = "rating.kp",
        @Query("sortType") sortType: String = "-1"
    ): MovieResponse

    @GET("v1.4/movie/search")
    suspend fun searchMovies(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10,
        @Query("query") query: String
    ): MovieResponse

    @GET("v1.4/movie/{id}")
    suspend fun getMovieById(
        @Path("id") id: Int
    ): MovieDto
}
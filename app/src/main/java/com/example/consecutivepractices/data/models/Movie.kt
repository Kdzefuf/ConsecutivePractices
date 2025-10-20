package com.example.consecutivepractices.data.models

import com.example.consecutivepractices.domain.models.Movie
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MovieResponse(
    val docs: List<MovieDto>,
    val total: Int,
    val limit: Int,
    val page: Int,
    val pages: Int
)

@JsonClass(generateAdapter = true)
data class MovieDto(
    val id: Int,
    val name: String?,
    val alternativeName: String?,
    val year: Int?,
    val rating: Rating?,
    val poster: Poster?,
    val genres: List<Genre>?,
    val countries: List<Country>?,
    val persons: List<Person>?,
    val description: String?
) {
    fun toMovie(): Movie {
        return Movie(
            id = id,
            title = name ?: alternativeName ?: "Неизвестное название",
            year = year ?: 0,
            rating = rating?.kp ?: 0.0,
            genre = genres?.joinToString { it.name ?: "" } ?: "Неизвестный жанр",
            director = getDirectorName(),
            synopsis = description ?: "Описание отсутствует",
            imageUrl = poster?.url ?: ""
        )
    }

    private fun getDirectorName(): String {
        return persons
            ?.filter {
                (it.profession == "режиссеры" || it.enProfession == "director") &&
                        it.name != null
            }
            ?.joinToString { it.name ?: "" }
            ?.takeIf { it.isNotBlank() }
            ?: "Неизвестный режиссер"
    }
}

@JsonClass(generateAdapter = true)
data class Rating(
    val kp: Double?
)

@JsonClass(generateAdapter = true)
data class Poster(
    val url: String?,
    val previewUrl: String?
)

@JsonClass(generateAdapter = true)
data class Genre(
    val name: String?
)

@JsonClass(generateAdapter = true)
data class Country(
    val name: String?
)

@JsonClass(generateAdapter = true)
data class Person(
    val id: Int,
    val name: String?,
    val profession: String?,
    val enProfession: String?
)
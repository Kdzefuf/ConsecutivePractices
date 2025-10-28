package com.example.consecutivepractices.data.mapper

import com.example.consecutivepractices.data.models.MovieDto
import com.example.consecutivepractices.domain.models.Movie
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieMapper @Inject constructor() {

    fun mapToDomain(dto: MovieDto): Movie {
        return Movie(
            id = dto.id,
            title = dto.name ?: dto.alternativeName ?: "",
            year = dto.year ?: 0,
            rating = dto.rating?.kp ?: 0.0,
            genre = dto.genres?.joinToString { it.name ?: "" } ?: "",
            director = getDirectorName(dto.persons),
            synopsis = dto.description ?: "",
            imageUrl = dto.poster?.url ?: ""
        )
    }

    private fun getDirectorName(persons: List<com.example.consecutivepractices.data.models.Person>?): String {
        return persons
            ?.filter {
                (it.profession == "режиссеры" || it.enProfession == "director") &&
                        it.name != null
            }
            ?.joinToString { it.name ?: "" }
            ?: ""
    }
}
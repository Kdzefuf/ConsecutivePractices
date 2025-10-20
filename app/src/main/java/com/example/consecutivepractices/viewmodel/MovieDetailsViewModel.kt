package com.example.consecutivepractices.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.consecutivepractices.data.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val movies = listOf(
        Movie(
            id = 1,
            title = "Начало",
            year = 2010,
            rating = 8.8,
            genre = "Научная фантастика",
            director = "Кристофер Нолан",
            synopsis = "Умопомрачительное ограбление...",
            imageUrl = "https://avatars.mds.yandex.net/get-kinopoisk-image/1629390/8ab9a119-dd74-44f0-baec-0629797483d7/600x900"
        ),
        Movie(
            id = 2,
            title = "Темный рыцарь",
            year = 2008,
            rating = 9.0,
            genre = "Боевик",
            director = "Кристофер Нолан",
            synopsis = "Битва с Джокером...",
            imageUrl = "https://avatars.mds.yandex.net/get-kinopoisk-image/1599028/0fa5bf50-d5ad-446f-a599-b26d070c8b99/600x900"
        ),
        Movie(
            id = 3,
            title = "Интерстеллар",
            year = 2014,
            rating = 8.6,
            genre = "Научная фантастика",
            director = "Кристофер Нолан",
            synopsis = "Путешествие через космос...",
            imageUrl = "https://avatars.mds.yandex.net/get-kinopoisk-image/1600647/78c36c0f-aefd-4102-bc3b-bac0dd4314d8/3840x"
        )
    )

    val movie: Movie?
        get() {
            val movieIdString = savedStateHandle.get<String>("movieId")
            return movieIdString?.toIntOrNull()?.let { movieId ->
                movies.find { it.id == movieId }
            }
        }

    fun shareMovie(context: Context) {
        movie?.let {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Посмотрите этот фильм!")
                putExtra(Intent.EXTRA_TEXT, "Фильм: ${it.title} (${it.year})\nРейтинг: ${it.rating}\nЖанр: ${it.genre}\nРежиссер: ${it.director}\nОписание: ${it.synopsis}\nПоделитесь этим потрясающим фильмом!")
            }
            context.startActivity(Intent.createChooser(shareIntent, "Поделиться"))
        }
    }
}
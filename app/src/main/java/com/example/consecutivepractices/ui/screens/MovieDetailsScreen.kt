package com.example.consecutivepractices.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.consecutivepractices.viewmodel.MovieViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.consecutivepractices.shareMovie

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(movieId: Int?, navController: androidx.navigation.NavController) {
    val viewModel: MovieViewModel = viewModel()
    val movie = movieId?.let { viewModel.getMovieById(it) }
    val context = LocalContext.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentMovieId = navBackStackEntry?.arguments?.getString("movieId")?.toIntOrNull()

    Column {
        TopAppBar(
            title = { Text("Movie Details") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = {
                    movie?.let { shareMovie(context, it) }
                }) {
                    Icon(Icons.Filled.Share, contentDescription = "Share")
                }
            }
        )

        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            movie?.let {
                if (it.imageUrl.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(bottom = 16.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(it.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Постер для ${it.title}",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                val details = listOf(
                    "Название" to it.title,
                    "Год" to it.year.toString(),
                    "Рейтинг" to it.rating.toString(),
                    "Жанр" to it.genre,
                    "Режиссер" to it.director,
                    "Описание" to it.synopsis
                )

                details.forEach { (label, value) ->
                    Text(
                        text = "$label: $value",
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            } ?: Text(text = "Фильм не найден")
        }
    }
}
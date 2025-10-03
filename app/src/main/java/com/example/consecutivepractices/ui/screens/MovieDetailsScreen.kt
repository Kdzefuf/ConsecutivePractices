package com.example.consecutivepractices.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
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
import com.example.consecutivepractices.shareMovie

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(movieId: Int?, navController: androidx.navigation.NavController) {
    val viewModel: MovieViewModel = viewModel()
    val movie = movieId?.let { viewModel.getMovieById(it) }
    val context = LocalContext.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val movieId = navBackStackEntry?.arguments?.getString("movieId")?.toIntOrNull()

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

        Column(modifier = Modifier.padding(16.dp)) {
            movie?.let {
                val details = listOf(
                    "Title" to it.title,
                    "Year" to it.year.toString(),
                    "Rating" to it.rating.toString(),
                    "Genre" to it.genre,
                    "Director" to it.director,
                    "Synopsis" to it.synopsis
                )
                details.forEach { (label, value) ->
                    Text(
                        text = "$label: $value",
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            } ?: Text(text = "Movie not found")
        }
    }

}
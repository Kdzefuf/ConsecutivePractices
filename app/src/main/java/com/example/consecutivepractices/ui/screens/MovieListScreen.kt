package com.example.consecutivepractices.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.consecutivepractices.viewmodel.MovieViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(navController: NavController) {
    val viewModel: MovieViewModel = viewModel()

    Column {
        TopAppBar(
            title = { Text("Movie List") },
            actions = {
                IconButton(onClick = { /* Add functionality later */ }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Movie")
                }
            },
        )

        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            LazyColumn {
                items(viewModel.movies) { movie ->
                    Row(modifier = Modifier.padding(6.dp).background(Color.Gray)) {
                        Text(text = "${movie.title} (${movie.year}) - ${movie.rating}",
                            modifier = Modifier.clickable {
                                navController.navigate("movie_details/${movie.id}")
                            }.padding(10.dp))
                    }
                }
            }
        }
    }
}
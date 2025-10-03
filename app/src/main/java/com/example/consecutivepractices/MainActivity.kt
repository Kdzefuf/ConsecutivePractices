package com.example.consecutivepractices

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.consecutivepractices.data.Movie
import com.example.consecutivepractices.ui.screens.MovieListScreen
import com.example.consecutivepractices.ui.screens.MovieDetailsScreen
import com.example.consecutivepractices.ui.theme.MovieAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MovieAppTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    bottomBar = {
                        if (currentRoute != "movie_details/{movieId}") {
                            BottomNavigationBar(navController, currentRoute)
                        }
                    }
                ) { padding ->
                    NavHost(
                        navController,
                        startDestination = "movie_list",
                        modifier = Modifier.padding(padding)
                    ) {
                        composable("movie_list") { MovieListScreen(navController) }
                        composable("movie_details/{movieId}") { backStackEntry ->
                            val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull()
                            MovieDetailsScreen(movieId, navController)
                        }
                    }
                }
            }
        }
    }
}

fun shareMovie(context: android.content.Context, movie: Movie) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Check out this movie!")
        putExtra(Intent.EXTRA_TEXT, "Movie: ${movie.title} (${movie.year})\nRating: ${movie.rating}\nGenre: ${movie.genre}\nDirector: ${movie.director}\nSynopsis: ${movie.synopsis}\nShare this awesome movie!")
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share via"))
}

@Composable
fun BottomNavigationBar(navController: androidx.navigation.NavController, currentRoute: String?) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = { navController.navigate("home") { launchSingleTop = true } },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = currentRoute == "movie_list",
            onClick = { navController.navigate("movie_list") { launchSingleTop = true } },
            icon = { Icon(Icons.Filled.Menu, contentDescription = "Movies") },
            label = { Text("Movies") }
        )
        NavigationBarItem(
            selected = currentRoute == "video",
            onClick = { navController.navigate("video") { launchSingleTop = true } },
            icon = { Icon(Icons.Filled.PlayArrow, contentDescription = "Video") },
            label = { Text("Video") }
        )
        NavigationBarItem(
            selected = currentRoute == "notifications",
            onClick = { navController.navigate("notifications") { launchSingleTop = true } },
            icon = { Icon(Icons.Filled.Notifications, contentDescription = "Notifications") },
            label = { Text("Bell") }
        )
    }
}


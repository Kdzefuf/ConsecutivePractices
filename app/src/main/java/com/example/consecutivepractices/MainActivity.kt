package com.example.consecutivepractices

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.consecutivepractices.data.preferences.FilterData
import com.example.consecutivepractices.navigation.NavRoutes
import com.example.consecutivepractices.ui.screens.FavoritesScreen
import com.example.consecutivepractices.ui.screens.FilterScreen
import com.example.consecutivepractices.ui.screens.MovieDetailsScreen
import com.example.consecutivepractices.ui.screens.MovieListScreen
import com.example.consecutivepractices.ui.theme.MovieAppTheme
import com.example.consecutivepractices.viewmodel.MovieListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MovieAppTheme {
                MovieApp()
            }
        }
    }
}

@Composable
fun MovieApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            when {
                currentRoute == NavRoutes.MOVIE_DETAILS -> MovieDetailsTopAppBar(navController)
                currentRoute == NavRoutes.FILTERS -> FilterTopAppBar(navController)
                currentRoute == NavRoutes.FAVORITES -> FavoritesTopAppBar(navController)
                currentRoute == NavRoutes.MOVIE_LIST -> MovieListTopAppBar(navController)
            }
        },
        bottomBar = {
            if (currentRoute in listOf(NavRoutes.MOVIE_LIST, NavRoutes.FAVORITES)) {
                BottomNavigationBar(navController, currentRoute)
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.MOVIE_LIST,
            modifier = Modifier.padding(padding)
        ) {
            composable(NavRoutes.MOVIE_LIST) {
                MovieListScreen(navController = navController)
            }
            composable(NavRoutes.MOVIE_DETAILS) { backStackEntry ->
                val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull()
                MovieDetailsScreen(
                    navController = navController,
                    movieId = movieId
                )
            }
            composable(NavRoutes.FILTERS) {
                FilterScreen(navController = navController)
            }
            composable(NavRoutes.FAVORITES) {
                FavoritesScreen(navController = navController)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListTopAppBar(navController: NavController) {
    val viewModel: MovieListViewModel = hiltViewModel()
    val filterData by viewModel.filterData.collectAsState()
    val showBadge = filterData.hasActiveFilters

    TopAppBar(
        title = { Text("Фильмы") },
        actions = {
            // Кнопка избранного
            IconButton(
                onClick = {
                    navController.navigate(NavRoutes.FAVORITES)
                }
            ) {
                Icon(Icons.Default.Favorite, contentDescription = "Избранное")
            }

            // Кнопка фильтров с бейджем
            IconButton(
                onClick = {
                    navController.navigate(NavRoutes.FILTERS)
                }
            ) {
                BadgedBox(
                    badge = {
                        if (showBadge) {
                            Badge()
                        }
                    }
                ) {
                    Icon(Icons.Default.FilterList, contentDescription = "Фильтры")
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsTopAppBar(navController: NavController) {
    TopAppBar(
        title = { Text("Детали фильма") },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterTopAppBar(navController: NavController) {
    TopAppBar(
        title = { Text("Фильтры") },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesTopAppBar(navController: NavController) {
    TopAppBar(
        title = { Text("Избранное") },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
            }
        }
    )
}

@Composable
fun BottomNavigationBar(navController: NavController, currentRoute: String?) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == NavRoutes.MOVIE_LIST,
            onClick = {
                navController.navigate(NavRoutes.MOVIE_LIST) {
                    launchSingleTop = true
                    // Очищаем бэкстек до корня
                    popUpTo(NavRoutes.MOVIE_LIST) { inclusive = true }
                }
            },
            icon = { Icon(Icons.Default.Home, contentDescription = "Фильмы") },
            label = { Text("Фильмы") }
        )
        NavigationBarItem(
            selected = currentRoute == NavRoutes.FAVORITES,
            onClick = {
                navController.navigate(NavRoutes.FAVORITES) {
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Default.Favorite, contentDescription = "Избранное") },
            label = { Text("Избранное") }
        )
    }
}
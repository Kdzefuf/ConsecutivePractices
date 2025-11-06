package com.example.consecutivepractices

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.consecutivepractices.navigation.NavRoutes
import com.example.consecutivepractices.ui.screens.EditProfileScreen
import com.example.consecutivepractices.ui.screens.FavoritesScreen
import com.example.consecutivepractices.ui.screens.FilterScreen
import com.example.consecutivepractices.ui.screens.MovieDetailsScreen
import com.example.consecutivepractices.ui.screens.MovieListScreen
import com.example.consecutivepractices.ui.screens.ProfileScreen
import com.example.consecutivepractices.ui.theme.MovieAppTheme
import com.example.consecutivepractices.viewmodel.MovieDetailsViewModel
import com.example.consecutivepractices.viewmodel.MovieListViewModel
import com.example.consecutivepractices.viewmodel.ProfileViewModel
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
    val context = LocalContext.current

    val profileViewModel: ProfileViewModel = hiltViewModel()

    Scaffold(
        topBar = {
            UnifiedTopAppBar(
                navController = navController,
                currentRoute = currentRoute,
                context = context,
                profileViewModel = profileViewModel,
                onEditProfileClick = {
                    navController.navigate(NavRoutes.EDIT_PROFILE)
                }
            )
        },
        bottomBar = {
            if (currentRoute in listOf(NavRoutes.MOVIE_LIST, NavRoutes.FAVORITES, NavRoutes.PROFILE)) {
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
                MovieDetailsScreen(navController = navController, movieId = movieId)
            }
            composable(NavRoutes.FILTERS) {
                FilterScreen(navController = navController)
            }
            composable(NavRoutes.FAVORITES) {
                FavoritesScreen(navController = navController)
            }
            composable(NavRoutes.PROFILE) {
                ProfileScreen(
                    navController = navController,
                    viewModel = profileViewModel
                )
            }
            composable(NavRoutes.EDIT_PROFILE) {
                EditProfileScreen(
                    navController = navController,
                    viewModel = profileViewModel
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnifiedTopAppBar(
    navController: NavController,
    currentRoute: String?,
    context: android.content.Context,
    profileViewModel: ProfileViewModel,
    onEditProfileClick: () -> Unit
) {
    val movieListViewModel: MovieListViewModel = hiltViewModel()
    val filterData by movieListViewModel.filterData.collectAsState()
    val showBadge = filterData.hasActiveFilters

    val movieDetailsViewModel: MovieDetailsViewModel = hiltViewModel()
    val movieDetailsState by movieDetailsViewModel.state.collectAsState()
    val isFavorite by movieDetailsViewModel.isFavorite.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    val isSearching = when (val state = movieListViewModel.state.collectAsState().value) {
        is com.example.consecutivepractices.ui.state.MovieListState.Success -> state.isSearching
        else -> false
    }

    TopAppBar(
        title = {
            when {
                currentRoute == NavRoutes.MOVIE_DETAILS -> {
                    Text("Детали фильма", maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                currentRoute == NavRoutes.FILTERS -> Text("Фильтры")
                currentRoute == NavRoutes.FAVORITES -> Text("Избранное")
                currentRoute == NavRoutes.PROFILE -> Text("Профиль")
                currentRoute == NavRoutes.EDIT_PROFILE -> Text("Редактирование профиля")
                currentRoute == NavRoutes.MOVIE_LIST && isSearching -> {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            if (it.isNotBlank()) {
                                movieListViewModel.searchMovies(it)
                            }
                        },
                        placeholder = { Text("Поиск фильмов...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                else -> Text("Фильмы")
            }
        },
        navigationIcon = {
            when {
                currentRoute == NavRoutes.EDIT_PROFILE -> {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
                currentRoute != NavRoutes.MOVIE_LIST -> {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
                else -> null
            }
        },
        actions = {
            when (currentRoute) {
                NavRoutes.MOVIE_LIST -> {
                    IconButton(
                        onClick = {
                            val newSearchState = !isSearching
                            movieListViewModel.setSearchState(newSearchState, searchQuery)
                            if (!newSearchState) {
                                searchQuery = ""
                                movieListViewModel.loadPopularMovies()
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = if (isSearching) "Закрыть поиск" else "Поиск"
                        )
                    }
                    IconButton(
                        onClick = {
                            if (isSearching && searchQuery.isNotBlank()) {
                                movieListViewModel.searchMovies(searchQuery)
                            } else {
                                movieListViewModel.loadPopularMovies()
                            }
                        }
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Обновить")
                    }
                    IconButton(
                        onClick = { navController.navigate(NavRoutes.FAVORITES) }
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = "Избранное")
                    }
                    IconButton(
                        onClick = { navController.navigate(NavRoutes.FILTERS) }
                    ) {
                        BadgedBox(
                            badge = { if (showBadge) Badge() }
                        ) {
                            Icon(Icons.Default.FilterList, contentDescription = "Фильтры")
                        }
                    }
                }

                NavRoutes.MOVIE_DETAILS -> {
                    if (movieDetailsState is com.example.consecutivepractices.ui.state.MovieDetailsState.Success) {
                        IconButton(onClick = { movieDetailsViewModel.toggleFavorite() }) {
                            Icon(
                                if (isFavorite) Icons.Default.Favorite else Icons.Rounded.FavoriteBorder,
                                contentDescription = if (isFavorite) "Удалить из избранного" else "Добавить в избранное",
                                tint = if (isFavorite) androidx.compose.ui.graphics.Color.Red else androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    if (movieDetailsState is com.example.consecutivepractices.ui.state.MovieDetailsState.Success) {
                        IconButton(onClick = { movieDetailsViewModel.shareMovie(context) }) {
                            Icon(Icons.Default.Share, contentDescription = "Поделиться")
                        }
                    }
                    if (movieDetailsState is com.example.consecutivepractices.ui.state.MovieDetailsState.Error) {
                        IconButton(onClick = { movieDetailsViewModel.retry() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Повторить")
                        }
                    }
                }

                NavRoutes.FAVORITES -> {
                    IconButton(onClick = { /* optional */ }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Обновить")
                    }
                }

                NavRoutes.FILTERS -> {
                    IconButton(onClick = { /* optional */ }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Сбросить фильтры")
                    }
                }

                NavRoutes.PROFILE -> {
                    IconButton(onClick = onEditProfileClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                    }
                }

                NavRoutes.EDIT_PROFILE -> {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "Сохранить")
                    }
                }
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
        NavigationBarItem(
            selected = currentRoute == NavRoutes.PROFILE,
            onClick = {
                navController.navigate(NavRoutes.PROFILE) {
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Default.Person, contentDescription = "Профиль") },
            label = { Text("Профиль") }
        )
    }
}
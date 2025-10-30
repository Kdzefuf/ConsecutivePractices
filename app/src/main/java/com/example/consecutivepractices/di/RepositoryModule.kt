package com.example.consecutivepractices.di

import com.example.consecutivepractices.data.repository.FavoriteRepositoryImpl
import com.example.consecutivepractices.data.repository.MovieRepositoryImpl
import com.example.consecutivepractices.domain.repository.FavoriteRepository
import com.example.consecutivepractices.domain.repository.MovieRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMovieRepository(
        movieRepositoryImpl: MovieRepositoryImpl
    ): MovieRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteRepository(
        favoriteRepositoryImpl: FavoriteRepositoryImpl
    ): FavoriteRepository
}
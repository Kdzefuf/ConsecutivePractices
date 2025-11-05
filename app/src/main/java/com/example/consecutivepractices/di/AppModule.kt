package com.example.consecutivepractices.di

import android.content.Context
import com.example.consecutivepractices.data.preferences.FavoritesPreferences
import com.example.consecutivepractices.data.preferences.FilterPreferences
import com.example.consecutivepractices.util.ErrorHandler
import com.example.consecutivepractices.util.FilterBadgeManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideErrorHandler(): ErrorHandler {
        return ErrorHandler()
    }

    @Provides
    @Singleton
    fun provideFilterPreferences(@ApplicationContext context: Context): FilterPreferences {
        return FilterPreferences(context)
    }

    @Provides
    @Singleton
    fun provideFavoritesPreferences(@ApplicationContext context: Context): FavoritesPreferences {
        return FavoritesPreferences(context)
    }

    @Provides
    @Singleton
    fun provideFilterBadgeManager(): FilterBadgeManager {
        return FilterBadgeManager()
    }
}
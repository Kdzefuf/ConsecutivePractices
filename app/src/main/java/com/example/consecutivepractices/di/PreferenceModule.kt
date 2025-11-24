package com.example.consecutivepractices.di

import com.example.consecutivepractices.data.preferences.ProfilePreferences
import com.example.consecutivepractices.data.preferences.ProfilePreferencesImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class PreferenceModule {

    @Binds
    @ViewModelScoped
    abstract fun bindProfilePreferences(impl: ProfilePreferencesImpl): ProfilePreferences
}
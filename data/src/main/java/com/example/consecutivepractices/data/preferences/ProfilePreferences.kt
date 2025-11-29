package com.example.consecutivepractices.data.preferences

import com.example.consecutivepractices.data.models.UserProfile

interface ProfilePreferences {
    suspend fun saveProfile(profile: UserProfile)
    suspend fun loadProfile(): UserProfile
}
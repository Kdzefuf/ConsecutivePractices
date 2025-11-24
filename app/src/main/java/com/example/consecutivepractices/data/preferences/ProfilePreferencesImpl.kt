package com.example.consecutivepractices.data.preferences

import android.content.Context
import com.example.consecutivepractices.data.models.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext

@Singleton
class ProfilePreferencesImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ProfilePreferences {

    private val prefs = context.getSharedPreferences("profile_prefs", Context.MODE_PRIVATE)

    override suspend fun saveProfile(profile: UserProfile) = withContext(Dispatchers.IO) {
        prefs.edit()
            .putString("full_name", profile.fullName)
            .putString("avatar_uri", profile.avatarUri)
            .putString("resume_url", profile.resumeUrl)
            .putString("position", profile.position)
            .apply()
    }

    override suspend fun loadProfile(): UserProfile = withContext(Dispatchers.IO) {
        UserProfile(
            fullName = prefs.getString("full_name", "") ?: "",
            avatarUri = prefs.getString("avatar_uri", "") ?: "",
            resumeUrl = prefs.getString("resume_url", "") ?: "",
            position = prefs.getString("position", "") ?: ""
        )
    }
}
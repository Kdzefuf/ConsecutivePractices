package com.example.consecutivepractices.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.consecutivepractices.data.models.UserProfile
import com.example.consecutivepractices.data.preferences.ProfilePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profilePreferences: ProfilePreferences
) : ViewModel() {

    private val _profile = MutableStateFlow(UserProfile())
    val profile: StateFlow<UserProfile> = _profile

    init {
        viewModelScope.launch {
            _profile.value = profilePreferences.loadProfile()
        }
    }

    fun updateProfile(newProfile: UserProfile) {
        _profile.value = newProfile
        viewModelScope.launch {
            profilePreferences.saveProfile(newProfile)
        }
    }
}
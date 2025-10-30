package com.example.consecutivepractices.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.consecutivepractices.data.preferences.FilterPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(
    private val filterPreferences: FilterPreferences
) : ViewModel() {

    val filterData: Flow<com.example.consecutivepractices.data.preferences.FilterData>
            = filterPreferences.filterData

    fun saveFilters(genre: String, minRating: String, year: String) {
        viewModelScope.launch {
            filterPreferences.saveFilters(genre, minRating, year)
        }
    }

    fun clearFilters() {
        viewModelScope.launch {
            filterPreferences.clearFilters()
        }
    }
}
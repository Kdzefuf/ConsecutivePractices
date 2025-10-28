package com.example.consecutivepractices.util

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FilterBadgeManager @Inject constructor() {

    private var _showBadge = false

    val showBadge: Boolean
        get() = _showBadge

    fun setBadgeVisibility(visible: Boolean) {
        _showBadge = visible
    }

    fun clearBadge() {
        _showBadge = false
    }
}
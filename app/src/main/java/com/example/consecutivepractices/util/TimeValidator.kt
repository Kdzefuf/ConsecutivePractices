package com.example.consecutivepractices.util

object TimeValidator {
    fun isValidTime(time: String): Boolean {
        return try {
            val parts = time.split(":")
            if (parts.size == 2) {
                val hour = parts[0].toInt()
                val minute = parts[1].toInt()
                hour in 0..23 && minute in 0..59
            } else {
                false
            }
        } catch (e: NumberFormatException) {
            false
        }
    }

    fun isTimeEmpty(time: String): Boolean {
        return time.isBlank()
    }
}
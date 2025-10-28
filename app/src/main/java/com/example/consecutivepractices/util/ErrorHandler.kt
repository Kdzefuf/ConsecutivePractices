package com.example.consecutivepractices.util

import kotlinx.coroutines.CoroutineExceptionHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ErrorHandler @Inject constructor() {

    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        println("Coroutine error: ${throwable.message}")
    }

    fun getErrorMessage(exception: Exception): String {
        return when {
            exception.message?.contains("404") == true -> "Данные не найдены"
            exception.message?.contains("401") == true -> "Ошибка авторизации"
            exception.message?.contains("500") == true -> "Ошибка сервера"
            exception.message?.contains("Unable to resolve host") == true -> "Проверьте подключение к интернету"
            else -> exception.message ?: "Произошла неизвестная ошибка"
        }
    }
}
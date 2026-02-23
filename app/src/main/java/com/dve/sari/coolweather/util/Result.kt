package com.dve.sari.coolweather.util

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()

    data class Error(
        val message: String,
        val exception: Throwable? = null
    ) : Result<Nothing>()

    data object Loading : Result<Nothing>()
}

fun <T> Result<T>.isSuccess(): Boolean = this is Result.Success

fun <T> Result<T>.isError(): Boolean = this is Result.Error

fun <T> Result<T>.getOrNull(): T? = if (this is Result.Success) data else null

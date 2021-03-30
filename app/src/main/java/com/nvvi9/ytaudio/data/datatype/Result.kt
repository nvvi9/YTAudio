package com.nvvi9.ytaudio.data.datatype


sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val throwable: Throwable) : Result<Nothing>()
}

inline fun <R> tryCatching(block: () -> R): Result<R> =
        try {
            Result.Success(block())
        } catch (t: Throwable) {
            Result.Error(t)
        }
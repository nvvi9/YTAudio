package com.nvvi9.ytaudio.vo


sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error<out T>(val t: Throwable) : Result<T>()
    class Loading<out T> : Result<T>()
}
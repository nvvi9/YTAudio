package com.example.service.domain

data class AudioItem @JvmOverloads constructor(
    val id: String,
    val title: String,
    val author: String,
    val thumbnailUri: String,
    val durationSeconds: String,
    var isPlaying: Boolean = false
)
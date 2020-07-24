package com.example.ytaudio.data.videodata


data class Details(
    val title: String,
    val channel: String,
    val channelId: String?,
    val tags: List<String>?,
    val durationSeconds: String,
    val description: String?,
    val rating: Float?,
    val viewCount: String?
)
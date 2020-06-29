package com.example.ytaudio.domain


data class SearchItem(
    val videoId: String,
    val title: String,
    val thumbnailUri: String,
    val channelTitle: String
)
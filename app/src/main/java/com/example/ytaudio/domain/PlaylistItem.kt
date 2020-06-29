package com.example.ytaudio.domain

import android.net.Uri

data class PlaylistItem(
    val id: String,
    val title: String,
    val author: String,
    val thumbnailUri: Uri,
    val duration: Long,
    val playbackState: Int
)
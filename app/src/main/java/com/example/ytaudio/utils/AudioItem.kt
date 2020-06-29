package com.example.ytaudio.utils

import android.net.Uri

data class AudioItem(
    val audioId: String,
    val title: String,
    val subtitle: String,
    val thumbnailUri: Uri,
    var playbackStatus: Int
)
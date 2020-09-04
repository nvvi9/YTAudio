package com.example.ytaudio.vo

import android.net.Uri


data class NowPlayingInfo(
    val id: String?,
    val title: String?,
    val author: String?,
    val thumbnailUri: Uri,
    val duration: Long,
    var currentPosition: Long = 0L,
    var audioButtonRes: Int? = null
)
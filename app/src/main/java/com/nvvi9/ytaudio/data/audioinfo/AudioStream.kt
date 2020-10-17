package com.nvvi9.ytaudio.data.audioinfo


data class AudioStream(
    val url: String,
    val extension: String,
    val codec: String,
    val bitrate: Int
)
package com.example.ytaudio.data.streamyt


data class StreamDetails(
    val itag: Int,
    val type: String,
    val extension: String?,
    val audioCodec: String?,
    val videoCodec: String?,
    val quality: Int?,
    val bitrate: Int?,
    val fps: Int?
)
package com.example.ytaudio.data.videodata


data class VideoStream(
    override val uri: String,
    override val codec: String?,
    override val extension: String?,
    override val itag: Int?,
    override val bitrate: Int?,
    override val averageBitrate: Int?,
    val fps: Int?,
    val size: String?,
    val quality: String?,
    val projection: String?
) : Stream
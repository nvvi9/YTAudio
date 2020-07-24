package com.example.ytaudio.data.videodata

data class AudioStream(
    override val uri: String,
    override val codec: String?,
    override val extension: String?,
    override val itag: Int?,
    override val bitrate: Int?,
    override val averageBitrate: Int?,
    val sampleRate: Int,
    val channels: Int?
) : Stream

package com.example.ytaudio.data.videodata

interface Stream {
    val uri: String
    val codec: String?
    val extension: String?
    val itag: Int?
    val bitrate: Int?
    val averageBitrate: Int?
}
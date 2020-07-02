package com.example.ytaudio.database.entities

import com.github.kotvertolet.youtubejextractor.models.AdaptiveAudioStream

data class AudioStream(
    val uri: String,
    val codec: String,
    val extension: String,
    val itag: Int,
    val bitrate: Int,
    val averageBitrate: Int,
    val channels: Int,
    val sampleRate: Int
) {

    constructor(stream: AdaptiveAudioStream) :
            this(
                stream.url, stream.codec,
                stream.extension, stream.getiTag(),
                stream.bitrate, stream.averageBitrate,
                stream.audioChannels, stream.audioSampleRate
            )
}
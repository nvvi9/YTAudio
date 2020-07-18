package com.example.ytaudio.data.audioinfo

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

    companion object {

        fun from(stream: AdaptiveAudioStream) =
            stream.run {
                AudioStream(
                    url, codec, extension, getiTag(), bitrate,
                    averageBitrate, audioChannels, audioSampleRate
                )
            }
    }
}
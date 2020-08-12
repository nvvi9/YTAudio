package com.example.ytaudio.data.audioinfo

import androidx.room.Ignore
import com.example.ytaudio.data.streamyt.Stream


data class AudioStream(
    val url: String,
    val extension: String,
    val codec: String,
    val bitrate: Int
) {
    companion object {
        @Ignore
        fun fromStream(stream: Stream): AudioStream {
            return with(stream) {
                if (stream.streamDetails.type != "AUDIO") throw IllegalArgumentException("AUDIO type expected")
                AudioStream(
                    url,
                    streamDetails.extension!!,
                    streamDetails.audioCodec!!,
                    streamDetails.bitrate!!
                )
            }
        }
    }
}
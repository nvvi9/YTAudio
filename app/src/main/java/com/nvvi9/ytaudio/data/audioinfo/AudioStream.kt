package com.nvvi9.ytaudio.data.audioinfo

import androidx.room.Ignore
import com.nvvi9.model.streams.Stream
import com.nvvi9.model.streams.StreamType


data class AudioStream(
    val url: String,
    val extension: String,
    val codec: String,
    val bitrate: Int
) {
    companion object {
        @Ignore
        fun fromStream(stream: Stream) =
            stream.takeIf { it.streamDetails.type == StreamType.AUDIO }?.run {
                AudioStream(
                    url, streamDetails.extension.toString(),
                    streamDetails.audioCodec!!.toString(), streamDetails.bitrate!!
                )
            }
    }
}

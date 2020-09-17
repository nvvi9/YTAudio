package com.nvvi9.ytstream.model.streams

import com.nvvi9.ytstream.model.codecs.AudioCodec
import com.nvvi9.ytstream.model.codecs.VideoCodec


data class StreamDetails(
    val itag: Int,
    val type: StreamType,
    val extension: Extension,
    val audioCodec: AudioCodec? = null,
    val videoCodec: VideoCodec? = null,
    val quality: Int? = null,
    val bitrate: Int? = null,
    val fps: Int? = if (type == StreamType.AUDIO) null else 30
)

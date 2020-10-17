package com.nvvi9.ytaudio.repositories.mapper

import com.nvvi9.model.VideoData
import com.nvvi9.model.streams.Stream
import com.nvvi9.model.streams.StreamType
import com.nvvi9.ytaudio.data.BaseMapper
import com.nvvi9.ytaudio.data.audioinfo.AudioDetails
import com.nvvi9.ytaudio.data.audioinfo.AudioInfo
import com.nvvi9.ytaudio.data.audioinfo.AudioStream


object AudioInfoMapper : BaseMapper<VideoData, AudioInfo> {

    override fun map(type: VideoData): AudioInfo? =
        with(type) {
            streams.filter { it.streamDetails.type == StreamType.AUDIO }
                .mapNotNull { it.toAudioStream() }
                .takeIf { it.isNotEmpty() }
                ?.let { streams ->
                    videoDetails.run {
                        AudioInfo(
                            id, AudioDetails(title, channel ?: "", durationSeconds ?: 0),
                            thumbnails, streams, expiresInSeconds,
                            System.currentTimeMillis() / 1000
                        )
                    }
                }
        }

    private fun Stream.toAudioStream() = takeIf { it.streamDetails.type == StreamType.AUDIO }?.run {
        AudioStream(
            url, streamDetails.extension.toString(),
            streamDetails.audioCodec!!.toString(), streamDetails.bitrate!!
        )
    }
}
package com.nvvi9.ytaudio.repositories.mapper

import com.nvvi9.model.VideoDetails
import com.nvvi9.ytaudio.data.BaseMapper
import com.nvvi9.ytaudio.data.ytstream.YTVideoDetails


object YTVideoDetailsMapper : BaseMapper<VideoDetails, YTVideoDetails> {

    override fun map(type: VideoDetails) =
        with(type) {
            YTVideoDetails(
                id, title, channel, channelId, description, durationSeconds,
                viewCount, thumbnails, expiresInSeconds, isLiveStream
            )
        }
}
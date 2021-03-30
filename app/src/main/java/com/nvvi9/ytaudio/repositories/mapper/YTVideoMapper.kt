package com.nvvi9.ytaudio.repositories.mapper

import com.nvvi9.model.VideoDetails
import com.nvvi9.ytaudio.data.BaseMapper
import com.nvvi9.ytaudio.data.ytstream.YTVideoItems


object YTVideoMapper : BaseMapper<VideoDetails, YTVideoItems.YTVideo> {

    override fun map(type: VideoDetails) =
        with(type) {
            YTVideoItems.YTVideo(
                    id, title, channel, channelId, description, durationSeconds,
                    viewCount, thumbnails, expiresInSeconds, isLiveStream
            )
        }
}
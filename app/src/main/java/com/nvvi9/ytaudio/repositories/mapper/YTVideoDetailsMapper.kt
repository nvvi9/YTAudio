package com.nvvi9.ytaudio.repositories.mapper

import com.nvvi9.model.VideoDetails
import com.nvvi9.ytaudio.data.BaseMapper
import com.nvvi9.ytaudio.data.ytstream.YTData


object YTVideoDetailsMapper : BaseMapper<VideoDetails, YTData> {

    override fun map(type: VideoDetails) =
        with(type) {
            YTData.YTVideoDetails(
                id, title, channel, channelId, description, durationSeconds,
                viewCount, thumbnails, expiresInSeconds, isLiveStream
            )
        }
}
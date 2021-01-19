package com.nvvi9.ytaudio.domain.mapper

import com.nvvi9.ytaudio.data.BaseMapper
import com.nvvi9.ytaudio.data.ytstream.YTData
import com.nvvi9.ytaudio.vo.YouTubeItem


object YouTubeItemMapper : BaseMapper<YTData.YTVideoDetails, YouTubeItem> {

    override fun map(type: YTData.YTVideoDetails) =
        with(type) {
            YouTubeItem(
                id, title ?: "", thumbnails[1].url,
                channel ?: "", viewCount, durationSeconds
            )
        }
}
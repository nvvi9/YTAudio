package com.nvvi9.ytaudio.domain.mapper

import com.nvvi9.ytaudio.data.BaseMapper
import com.nvvi9.ytaudio.data.ytstream.YTVideoDetails
import com.nvvi9.ytaudio.vo.YouTubeItem


object YouTubeItemMapper : BaseMapper<YTVideoDetails, YouTubeItem> {

    override fun map(type: YTVideoDetails) =
        with(type) {
            YouTubeItem(
                id, title ?: "", thumbnails[1].url,
                channel ?: "", viewCount, durationSeconds
            )
        }
}
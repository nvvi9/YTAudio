package com.nvvi9.ytaudio.domain.mapper

import com.nvvi9.ytaudio.data.BaseMapper
import com.nvvi9.ytaudio.data.ytstream.YTVideoItems
import com.nvvi9.ytaudio.vo.YouTubeItem


object YouTubeItemMapper : BaseMapper<YTVideoItems, YouTubeItem> {

    override fun map(type: YTVideoItems): YouTubeItem =
            with(type) {
                when (this) {
                    is YTVideoItems.YTVideo -> YouTubeItem.YouTubeVideoItem(
                            videoId, title ?: "", thumbnails.maxByOrNull { it.height }?.url,
                            channelTitle ?: "", viewCount, durationSeconds
                    )
                    is YTVideoItems.YTPlaylist -> YouTubeItem.YouTubePlaylistItem(
                            playlistId, title, description, thumbnails.maxByOrNull { it.height }?.url, channelTitle
                    )
                }
            }
}
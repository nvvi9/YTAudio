package com.nvvi9.ytaudio.repositories.mapper

import com.nvvi9.model.Thumbnail
import com.nvvi9.ytaudio.data.BaseMapper
import com.nvvi9.ytaudio.data.youtube.Item
import com.nvvi9.ytaudio.data.ytstream.YTVideoItems

object YTPlaylistMapper : BaseMapper<Item, YTVideoItems.YTPlaylist> {

    override fun map(type: Item): YTVideoItems.YTPlaylist? =
            type.id.playlistId?.let { playlistId ->
                type.snippet.run {
                    YTVideoItems.YTPlaylist(
                            playlistId, title, channelId,
                            channelTitle, description,
                            thumbnails.run {
                                listOf(
                                        Thumbnail(default.width, default.height, default.url),
                                        Thumbnail(medium.width, medium.height, medium.url),
                                        Thumbnail(high.width, high.height, high.url)
                                ).let { thumbs ->
                                    thumbs + (standard?.let {
                                        listOf(Thumbnail(it.width, it.height, it.url))
                                    } ?: emptyList()) + (maxres?.let {
                                        listOf(Thumbnail(it.width, it.height, it.url))
                                    } ?: emptyList())
                                }
                            }, publishedAt)
                }
            }
}
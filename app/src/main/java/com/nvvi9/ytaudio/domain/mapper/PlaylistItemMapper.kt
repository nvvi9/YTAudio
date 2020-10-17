package com.nvvi9.ytaudio.domain.mapper

import com.nvvi9.ytaudio.data.BaseMapper
import com.nvvi9.ytaudio.data.audioinfo.AudioInfo
import com.nvvi9.ytaudio.vo.PlaylistItem


object PlaylistItemMapper : BaseMapper<AudioInfo, PlaylistItem> {

    override fun map(type: AudioInfo) =
        with(type) {
            PlaylistItem(
                id, details.title, details.author,
                thumbnails.getOrNull(1)?.url,
                details.duration
            )
        }
}
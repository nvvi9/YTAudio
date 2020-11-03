package com.nvvi9.ytaudio.repositories.mapper

import com.nvvi9.ytaudio.data.BaseMapper
import com.nvvi9.ytaudio.data.youtube.YTPlaylistItems
import com.nvvi9.ytaudio.data.ytstream.YTData


object YTPlaylistItemMapper : BaseMapper<YTPlaylistItems, YTData> {

    override fun map(type: YTPlaylistItems): YTData? =
        with(type) {

        }
}
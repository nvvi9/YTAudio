package com.nvvi9.ytaudio.domain.mapper

import android.support.v4.media.MediaMetadataCompat
import com.nvvi9.ytaudio.data.BaseMapper
import com.nvvi9.ytaudio.data.audioinfo.AudioInfo
import com.nvvi9.ytaudio.utils.extensions.from


object MediaMetadataMapper : BaseMapper<AudioInfo, MediaMetadataCompat> {

    override fun map(type: AudioInfo) =
        MediaMetadataCompat.Builder().from(type).build()
}
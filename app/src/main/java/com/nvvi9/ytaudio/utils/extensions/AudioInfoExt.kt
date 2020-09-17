package com.nvvi9.ytaudio.utils.extensions

import android.support.v4.media.MediaMetadataCompat
import com.nvvi9.ytaudio.data.audioinfo.AudioInfo
import com.nvvi9.ytaudio.vo.PlaylistItem


fun Iterable<AudioInfo>.toMediaMetadataList() =
    map { MediaMetadataCompat.Builder().from(it).build() }

fun Iterable<AudioInfo>.toPlaylistItemList() =
    map { PlaylistItem.from(it) }
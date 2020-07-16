package com.example.ytaudio.utils.extensions

import android.support.v4.media.MediaMetadataCompat
import com.example.ytaudio.database.entities.AudioInfo
import com.example.ytaudio.domain.PlaylistItem


fun Iterable<AudioInfo>.toMediaMetadataList() =
    map { MediaMetadataCompat.Builder().from(it).build() }

fun Iterable<AudioInfo>.toPlaylistItemList() =
    map { PlaylistItem.from(it) }
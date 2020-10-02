package com.nvvi9.ytaudio.utils.extensions

import android.support.v4.media.MediaMetadataCompat
import com.nvvi9.ytaudio.data.audioinfo.AudioInfo


fun Iterable<AudioInfo>.toMediaMetadataList() =
    map { MediaMetadataCompat.Builder().from(it).build() }


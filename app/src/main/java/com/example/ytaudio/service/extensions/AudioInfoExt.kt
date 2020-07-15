package com.example.ytaudio.service.extensions

import android.support.v4.media.MediaMetadataCompat
import com.example.ytaudio.database.entities.AudioInfo


fun Iterable<AudioInfo>.toMediaMetadataList(): List<MediaMetadataCompat> =
    map { MediaMetadataCompat.Builder().from(it).build() }

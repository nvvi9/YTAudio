package com.example.ytaudio.service.library

import android.support.v4.media.MediaMetadataCompat


interface AudioSource : Iterable<MediaMetadataCompat> {

    fun whenReady(performAction: (Boolean) -> Unit): Boolean
}
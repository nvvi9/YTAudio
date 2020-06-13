package com.example.ytaudio.service.library

import android.content.Context
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.support.v4.media.MediaDescriptionCompat.STATUS_NOT_DOWNLOADED
import android.support.v4.media.MediaMetadataCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.ytaudio.R
import com.example.ytaudio.database.AudioDatabaseDao
import com.example.ytaudio.database.AudioInfo
import com.example.ytaudio.service.extensions.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DatabaseAudioSource(context: Context, private val database: AudioDatabaseDao) :
    AbstractAudioSource() {

    private var catalog: List<MediaMetadataCompat> = emptyList()
    private val glide: RequestManager

    init {
        state = STATE_INITIALIZING
        glide = Glide.with(context)
    }

    override fun iterator(): Iterator<MediaMetadataCompat> = catalog.iterator()

    override suspend fun load() {
        updateCatalog(database)?.let {
            catalog = it
            state = STATE_INITIALIZED
        } ?: run {
            catalog = emptyList()
            state = STATE_ERROR
        }
    }

    private suspend fun updateCatalog(database: AudioDatabaseDao): List<MediaMetadataCompat>? {
        return withContext(Dispatchers.IO) {
            val audioInfoList = database.getAllAudioInfo()

            audioInfoList.map {
                val artUri = glide.applyDefaultRequestOptions(glideOptions)
                    .downloadOnly()
                    .load(it.photoUrl)
                    .submit(NOTIFICATION_LARGE_ICON_SIZE, NOTIFICATION_LARGE_ICON_SIZE)
                    .get()
                    .asArtContentUri()

                MediaMetadataCompat.Builder()
                    .from(it)
                    .apply {
                        displayIconUri = it.photoUrl
                        albumArtUri = it.photoUrl
                    }
                    .build()
            }.toList()
        }
    }
}


fun MediaMetadataCompat.Builder.from(audioInfo: AudioInfo): MediaMetadataCompat.Builder {

    id = audioInfo.audioId.toString()
    title = audioInfo.audioTitle
    artist = audioInfo.author
    duration = audioInfo.audioDurationSeconds
    mediaUri = audioInfo.audioUrl
    displayIconUri = audioInfo.photoUrl
    flag = MediaItem.FLAG_PLAYABLE

    downloadStatus = STATUS_NOT_DOWNLOADED

    return this
}


class AudioInfoCatalog {
    var audio: List<AudioInfoFormatted> = ArrayList()
}


class AudioInfoFormatted {
    var audioId = ""
    var audioTitle = ""
    var author = ""
    var audioUri = ""
    var thumbnailUri = ""
    var duration = ""
}

private val List<AudioInfo>.formatted: List<AudioInfoFormatted>
    get() {
        val audioInfoFormattedList = mutableListOf<AudioInfoFormatted>()
        forEach {
            audioInfoFormattedList.add(AudioInfoFormatted().apply {
                audioId = it.audioId.toString()
                audioTitle = it.audioTitle
                author = it.author
                audioUri = it.audioUrl
                thumbnailUri = it.photoUrl
                duration = it.audioDurationSeconds.toString()
            })
        }
        return audioInfoFormattedList
    }

private const val NOTIFICATION_LARGE_ICON_SIZE = 144

private val glideOptions = RequestOptions()
    .fallback(R.drawable.ic_notification)
    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
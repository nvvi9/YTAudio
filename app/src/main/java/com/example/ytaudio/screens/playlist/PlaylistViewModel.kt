package com.example.ytaudio.screens.playlist

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.example.ytaudio.database.AudioDatabaseDao
import com.example.ytaudio.database.AudioInfo
import com.github.kotvertolet.youtubejextractor.YoutubeJExtractor
import com.github.kotvertolet.youtubejextractor.models.youtube.videoData.YoutubeVideoData
import kotlinx.coroutines.*


class PlaylistViewModel(
    val database: AudioDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private val extractor = YoutubeJExtractor()


    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val audioPlaylist = database.getAllAudio()


    init {
        updatePlaylistInfo()
    }


    private fun updatePlaylistInfo() {
        uiScope.launch {
            val startTimeMillis = System.currentTimeMillis()
            val audioInfoList = database.getAllAudioInfo()

            audioInfoList.forEach {
                it.updateInfo()
                database.update(it)
            }

            Toast.makeText(
                getApplication(),
                ((System.currentTimeMillis() - startTimeMillis).toDouble() / 1000).toString(),
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private suspend fun AudioInfo.updateInfo() {
        withContext(Dispatchers.IO) {
            val videoData = extractor.extract(youtubeId)
            val adaptiveAudioStream = videoData.streamingData.adaptiveAudioStreams.maxBy {
                it.averageBitrate
            }
            videoData.run {
                audioUrl = adaptiveAudioStream!!.url
                photoUrl = videoDetails.thumbnail.thumbnails.maxBy { it.height }!!.url
                audioTitle = videoDetails.title
                author = videoDetails.author
                authorId = videoDetails.channelId
                description = videoDetails.shortDescription
                keywords = videoDetails.keywords.joinToString()
                viewCount = videoDetails.viewCount.toIntOrNull() ?: 0
                averageRating = videoDetails.averageRating
                audioFormat = adaptiveAudioStream.extension
                codec = adaptiveAudioStream.codec
                bitrate = adaptiveAudioStream.bitrate
                averageBitrate = adaptiveAudioStream.averageBitrate
                audioDurationSeconds = videoDetails.lengthSeconds.toLong()
                lastUpdateTimeSeconds = System.currentTimeMillis() / 1000
                urlActiveTimeSeconds = streamingData.expiresInSeconds.toLong()
            }
        }
    }
}
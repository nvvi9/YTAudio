package com.example.ytaudio.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.ytaudio.database.AudioDatabaseDao
import com.example.ytaudio.database.entities.AudioInfo
import com.example.ytaudio.domain.PlaylistItem
import com.example.ytaudio.network.extractor.YTExtractor
import com.example.ytaudio.service.MediaPlaybackServiceConnection
import com.example.ytaudio.service.extensions.id
import com.example.ytaudio.service.extensions.isPlayEnabled
import com.example.ytaudio.service.extensions.isPlaying
import com.example.ytaudio.service.extensions.isPrepared
import com.example.ytaudio.utils.extensions.mapParallel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AudioRepository @Inject constructor(
    private val databaseDao: AudioDatabaseDao,
    val mediaPlaybackServiceConnection: MediaPlaybackServiceConnection,
    ytExtractor: YTExtractor
) : BaseRepository(ytExtractor) {

    val audioInfoList = databaseDao.getAllAudio()

    val playlistItems: LiveData<List<PlaylistItem>> =
        Transformations.map(databaseDao.getAllAudio()) { list ->
            list?.map { PlaylistItem.from(it) }
        }

    fun playAudio(audioId: String, pauseAllowed: Boolean = true) {
        val nowPlaying = mediaPlaybackServiceConnection.nowPlaying.value
        val transportControls = mediaPlaybackServiceConnection.transportControls

        val isPrepared = mediaPlaybackServiceConnection.playbackState.value?.isPrepared ?: false

        if (isPrepared && audioId == nowPlaying?.id) {
            mediaPlaybackServiceConnection.playbackState.value?.let {
                when {
                    it.isPlaying -> if (pauseAllowed) transportControls.pause() else Unit
                    it.isPlayEnabled -> transportControls.play()
                }
            }
        } else {
            transportControls.playFromMediaId(audioId, null)
        }
    }

    suspend fun updateAllAudioInfo() {
        withContext(Dispatchers.IO) {
            val startTime = System.nanoTime()
            val audioInfoList =
                databaseDao.getAllAudioInfo().mapParallel(Dispatchers.IO) {
                    extractAudioInfo(it.youtubeId)
                }.filterNotNull()
            databaseDao.update(audioInfoList)
            Log.i(
                javaClass.simpleName,
                "Full database update in ${(System.nanoTime() - startTime) / 1e6} ms"
            )
        }
    }

    suspend fun updateAudioInfoList(audioList: List<AudioInfo>) {
        withContext(Dispatchers.IO) {
            val startTime = System.nanoTime()
            val list = audioList.mapParallel(Dispatchers.IO) {
                extractAudioInfo(it.youtubeId)
            }.filterNotNull()
            databaseDao.update(list)
            Log.i(
                javaClass.simpleName,
                "${audioList.size} items updated in ${(System.nanoTime() - startTime) / 1e6} ms"
            )
        }
    }

    suspend fun deleteFromDatabase(idList: List<String>) {
        withContext(Dispatchers.IO) {
            databaseDao.delete(idList)
        }
    }
}
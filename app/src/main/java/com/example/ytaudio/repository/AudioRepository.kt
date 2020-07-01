package com.example.ytaudio.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.ytaudio.database.AudioDatabaseDao
import com.example.ytaudio.database.AudioInfo
import com.example.ytaudio.database.asPlaylistItems
import com.example.ytaudio.domain.PlaylistItem
import com.example.ytaudio.utils.extensions.forEachParallel
import com.github.kotvertolet.youtubejextractor.exception.ExtractionException
import com.github.kotvertolet.youtubejextractor.exception.YoutubeRequestException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AudioRepository(private val databaseDao: AudioDatabaseDao) {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val nowUpdatingSet = mutableSetOf<AudioInfo>()

    val playlistItems: LiveData<List<PlaylistItem>> =
        Transformations.map(databaseDao.getAllAudio()) {
            it?.asPlaylistItems()
        }

    fun updateAllAudioInfo() {
        coroutineScope.launch {
            val audioInfoList = databaseDao.getAllAudioInfo()
            runUpdate(audioInfoList)
        }
    }

    fun updateAudioInfoList(audioList: List<AudioInfo>) {
        coroutineScope.launch {
            runUpdate(audioList)
        }
    }

    private suspend fun runUpdate(list: List<AudioInfo>) {
        if (nowUpdatingSet.addAll(list)) {
            withContext(Dispatchers.IO) {
                val startTime = System.nanoTime()
                var updatedSuccessfully = 0
                list.forEachParallel {
                    try {
                        it.update()
                        updatedSuccessfully++
                    } catch (e: ExtractionException) {
                        Log.e(javaClass.simpleName, "${it.title} extraction failed")
                        databaseDao.delete(it)
                    } catch (e: YoutubeRequestException) {
                        Log.e(
                            javaClass.simpleName,
                            "network failure while updating ${it.title}"
                        )
                    } catch (e: Exception) {
                        Log.e(javaClass.simpleName, e.toString())
                    }
                }
                databaseDao.update(list)
                nowUpdatingSet.removeAll(list)
                Log.i(
                    javaClass.simpleName,
                    "$updatedSuccessfully/${list.size} items updated in ${(System.nanoTime() - startTime) / 10e5} ms"
                )
            }
        }
    }
}
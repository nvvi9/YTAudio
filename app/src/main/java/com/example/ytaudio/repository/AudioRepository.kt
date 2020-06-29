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

class AudioRepository(private val databaseDao: AudioDatabaseDao) {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val nowUpdatingSet = mutableSetOf<AudioInfo>()

    val playlistItems: LiveData<List<PlaylistItem>> =
        Transformations.map(databaseDao.getAllAudio()) {
            it?.asPlaylistItems()
        }

    fun updateAudioInfoList(audioList: List<AudioInfo>) {
        if (nowUpdatingSet.addAll(audioList)) {
            coroutineScope.launch {
                val startTime = System.nanoTime()
                var updatedSuccessfully = 0
                audioList.forEachParallel {
                    try {
                        it.update()
                        updatedSuccessfully++
                    } catch (e: ExtractionException) {
                        Log.e(javaClass.simpleName, "${it.audioTitle} extraction failed")
                        databaseDao.delete(it)
                    } catch (e: YoutubeRequestException) {
                        Log.e(
                            javaClass.simpleName,
                            "network failure while updating ${it.audioTitle}"
                        )
                    } catch (e: Exception) {
                        Log.e(javaClass.simpleName, e.toString())
                    }
                }
                databaseDao.update(audioList)
                nowUpdatingSet.removeAll(audioList)
                Log.i(
                    javaClass.simpleName,
                    "$updatedSuccessfully/${audioList.size} items updated in ${(System.nanoTime() - startTime) / 10e5} ms"
                )
            }
        }
    }
}
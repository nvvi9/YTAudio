package com.example.ytaudio.repositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.ytaudio.database.AudioDatabase
import com.example.ytaudio.database.entities.AudioInfo
import com.example.ytaudio.domain.PlaylistItem
import com.example.ytaudio.network.extractor.YTExtractor
import com.example.ytaudio.utils.extensions.mapParallel
import com.github.kotvertolet.youtubejextractor.exception.ExtractionException
import com.github.kotvertolet.youtubejextractor.exception.YoutubeRequestException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


class AudioRepository(context: Context) {

    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    private val databaseDao = AudioDatabase.getInstance(context).audioDatabaseDao

    val audioInfoList = databaseDao.getAllAudio()

    val playlistItems: LiveData<List<PlaylistItem>> =
        Transformations.map(audioInfoList) { list ->
            list?.map { it.toPlaylistItem() }
        }

    fun updateAllAudioInfo() {
        coroutineScope.launch {
            val startTime = System.nanoTime()
            val audioInfoList =
                databaseDao.getAllAudioInfo().mapParallel {
                    try {
                        AudioInfo(YTExtractor().extract(it.youtubeId))
                    } catch (e: ExtractionException) {
                        Log.e(javaClass.simpleName, "${it.title} extraction failed")
                        databaseDao.delete(it)
                        null
                    } catch (e: YoutubeRequestException) {
                        Log.e(javaClass.simpleName, "network failure")
                        null
                    } catch (e: Exception) {
                        Log.e(javaClass.simpleName, e.toString())
                        null
                    }
                }
            databaseDao.update(audioInfoList.filterNotNull())
            Log.i(
                javaClass.simpleName,
                "Full database update in ${(System.nanoTime() - startTime) / 1e6} ms"
            )
        }
    }

    fun updateAudioInfoList(audioList: List<AudioInfo>) {
        coroutineScope.launch {
            val startTime = System.nanoTime()
            val list = audioList.mapParallel {
                try {
                    AudioInfo(YTExtractor().extract(it.youtubeId))
                } catch (e: ExtractionException) {
                    Log.e(javaClass.simpleName, "${it.title} extraction failed")
                    databaseDao.delete(it)
                    null
                } catch (e: YoutubeRequestException) {
                    Log.e(javaClass.simpleName, "network failure")
                    null
                } catch (e: Exception) {
                    Log.e(javaClass.simpleName, e.toString())
                    null
                }
            }
            databaseDao.update(list.filterNotNull())
            Log.i(
                javaClass.simpleName,
                "${audioList.size} items updated in ${(System.nanoTime() - startTime) / 1e6} ms"
            )
        }
    }
}
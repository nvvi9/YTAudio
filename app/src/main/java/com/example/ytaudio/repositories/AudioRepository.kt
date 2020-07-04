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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class AudioRepository(context: Context) {

    private val databaseDao = AudioDatabase.getInstance(context).audioDatabaseDao

    val audioInfoList = databaseDao.getAllAudio()

    val playlistItems: LiveData<List<PlaylistItem>> =
        Transformations.map(databaseDao.getAllAudio()) { list ->
            list?.map { it.toPlaylistItem() }
        }

    suspend fun updateAllAudioInfo() {
        withContext(Dispatchers.IO) {
            val startTime = System.nanoTime()
            val audioInfoList =
                databaseDao.getAllAudioInfo().mapParallel(Dispatchers.IO) {
                    try {
                        YTExtractor().extractAudioInfo(it.youtubeId)
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

    suspend fun deleteFromDatabase(idList: List<String>) {
        withContext(Dispatchers.IO) {
            databaseDao.delete(idList)
        }
    }
}
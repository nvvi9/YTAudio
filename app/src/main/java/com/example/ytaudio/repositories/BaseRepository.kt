package com.example.ytaudio.repositories

import android.content.Context
import android.util.Log
import com.example.ytaudio.database.AudioDatabase
import com.example.ytaudio.database.entities.AudioInfo
import com.example.ytaudio.network.NetworkService
import com.example.ytaudio.utils.LiveContentException
import com.example.ytaudio.utils.UriAliveTimeMissException
import com.github.kotvertolet.youtubejextractor.exception.ExtractionException
import com.github.kotvertolet.youtubejextractor.exception.YoutubeRequestException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


open class BaseRepository(context: Context) {

    protected val databaseDao = AudioDatabase.getInstance(context).audioDatabaseDao

    protected suspend fun extractAudioInfo(id: String): AudioInfo? =
        withContext(Dispatchers.IO) {
            try {
                NetworkService.ytExtractor.extractAudioInfo(id)
            } catch (e: ExtractionException) {
                Log.e(javaClass.simpleName, "id: $id extraction failed")
                null
            } catch (e: YoutubeRequestException) {
                Log.e(javaClass.simpleName, "network failure")
                null
            } catch (e: LiveContentException) {
                Log.e(javaClass.simpleName, e.message!!)
                null
            } catch (e: UriAliveTimeMissException) {
                Log.e(javaClass.simpleName, e.message!!)
                null
            } catch (e: Exception) {
                Log.e(javaClass.simpleName, e.toString())
                null
            }
        }
}
package com.example.ytaudio.screens.url_link

import android.annotation.SuppressLint
import android.app.Application
import android.database.sqlite.SQLiteConstraintException
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.commit451.youtubeextractor.Stream
import com.commit451.youtubeextractor.YouTubeExtraction
import com.commit451.youtubeextractor.YouTubeExtractor
import com.example.ytaudio.database.AudioDatabaseDao
import com.example.ytaudio.database.AudioInfo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*

class SourceLinkViewModel(
    val database: AudioDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private val extractor = YouTubeExtractor.Builder().build()

    private val viewModelJob = Job()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _navigateToPlaylist = MutableLiveData(false)
    val navigateToPlaylist: LiveData<Boolean>
        get() = _navigateToPlaylist


    fun navigationDone() {
        _navigateToPlaylist.value = false
    }


    @SuppressLint("CheckResult")
    fun onExtract(videoLink: String) {
        val youtubeId = videoLink.takeLastWhile { it != '=' && it != '/' }
        extractor.extract(youtubeId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({ extraction ->
                passResult(extraction)
            }, { t ->
                onError(t)
            })
    }

/*
    fun extract(videoLink: String) {
        onExtract(videoLink, { result, youtubeId ->
            val url = result.streams.filterIsInstance<Stream.AudioStream>().filter {
                listOf(
                    Stream.FORMAT_M4A,
                    Stream.FORMAT_MPEG_4
                ).any { streamType -> streamType == it.format }
            }.takeIf { it.isNotEmpty() }?.get(0)?.url

            uiScope.launch {
                try {
                    insert(
                        AudioInfo(
                            youtubeId = youtubeId,
                            audioUri = url!!,
                            photoUri = result.thumbnails.first().url,
                            audioTitle = result.title!!
                        )
                    )
                } catch (error: SQLiteConstraintException) {
                    Toast.makeText(
                        getApplication(),
                        "${result.title} was already added",
                        Toast.LENGTH_SHORT
                    ).show()
                } finally {
                    _navigateToPlaylist.value = true
                }
            }
        },
            { t ->
                t.printStackTrace()
                Toast.makeText(getApplication(), "Extraction failed", Toast.LENGTH_LONG).show()
            })
    }
*/

    private fun onError(t: Throwable) {
        t.printStackTrace()
        Toast.makeText(getApplication(), "Extraction failed", Toast.LENGTH_LONG).show()
    }


    private fun passResult(result: YouTubeExtraction) {
        val url = result.streams.filterIsInstance<Stream.AudioStream>().filter {
            listOf(
                Stream.FORMAT_M4A,
                Stream.FORMAT_MPEG_4
            ).any { streamType -> streamType == it.format }
        }.takeIf { it.isNotEmpty() }?.get(0)?.url

        uiScope.launch {
            try {
                insert(
                    AudioInfo(
                        youtubeId = result.videoId,
                        audioUri = url!!,
                        photoUri = result.thumbnails.first().url,
                        audioTitle = result.title ?: "",
                        author = result.author ?: "",
                        description = result.description ?: "",
                        audioDuration = result.durationMilliseconds ?: 0L,
                        viewCount = result.viewCount ?: 0L
                    )
                )
            } catch (error: SQLiteConstraintException) {
                Toast.makeText(
                    getApplication(),
                    "${result.title} was already added",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                _navigateToPlaylist.value = true
            }
        }
    }

    private suspend fun insert(audio: AudioInfo) {
        withContext(Dispatchers.IO) {
            database.insert(audio)
        }
    }
}
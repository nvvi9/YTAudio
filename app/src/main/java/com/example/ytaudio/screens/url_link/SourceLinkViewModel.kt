package com.example.ytaudio.screens.url_link

import android.annotation.SuppressLint
import android.app.Application
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

//    private var audio = MutableLiveData<AudioInfo?>()

    private val _navigateToPlayer = MutableLiveData<Boolean>()

    val navigateToPlayer: LiveData<Boolean>
        get() = _navigateToPlayer

    private val _showToastEvent = MutableLiveData<Boolean>()

    val showToastEvent: LiveData<Boolean>
        get() = _showToastEvent

    init {
        _navigateToPlayer.value = false
    }


    @SuppressLint("CheckResult")
    fun onExtract(videoLink: String) {
        val videoId = videoLink.takeWhile { it != '=' && it != '/' }
        extractor.extract(videoId).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({ extraction ->
                passResult(extraction)
            }, { t ->
                onError(t)
            })
    }

    private fun onError(t: Throwable) {
        t.printStackTrace()
        _showToastEvent.value = true
    }

    fun doneShowingToast() {
        _showToastEvent.value = false
    }

    private fun passResult(result: YouTubeExtraction) {
        val url = result.streams.filterIsInstance<Stream.AudioStream>().filter {
            listOf(
                Stream.FORMAT_M4A,
                Stream.FORMAT_MPEG_4
            ).any { streamType -> streamType == it.format }
        }.takeIf { it.isNotEmpty() }?.get(0)?.url

        uiScope.launch {
//            val audio = AudioInfo()
//            audio.
            insert(
                AudioInfo(
                    audioUri = url!!,
                    photoUri = result.thumbnails.first().url,
                    audioTitle = result.title!!
                )
            )
        }
        _navigateToPlayer.value = true
    }

    private suspend fun insert(audio: AudioInfo) {
        withContext(Dispatchers.IO) {
            database.insert(audio)
        }
    }
}
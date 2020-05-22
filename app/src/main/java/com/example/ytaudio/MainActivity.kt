package com.example.ytaudio

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Contacts
import android.widget.Toast
import co.metalab.asyncawait.async
import co.metalab.asyncawait.await
import com.commit451.youtubeextractor.Stream
import com.commit451.youtubeextractor.YouTubeExtraction
import com.commit451.youtubeextractor.YouTubeExtractor
import com.example.ytaudio.database.AudioDatabase
import com.example.ytaudio.database.AudioDatabaseDao
import com.example.ytaudio.database.AudioInfo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var database: AudioDatabaseDao
    private val extractor = YouTubeExtractor.Builder().build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val application = requireNotNull(this).application
        database = AudioDatabase.getInstance(application).audioDatabaseDao


        setContentView(R.layout.activity_main)
    }

    @SuppressLint("CheckResult")
    private fun extract(audio: AudioInfo) {

        extractor.extract(audio.youtubeId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({ extraction ->
                passResult(extraction, audio)
            }, { t ->
                onError(t)
            })
    }

    private fun onError(t: Throwable) {
        t.printStackTrace()
        Toast.makeText(this, "Extraction failed", Toast.LENGTH_SHORT).show()
    }

    private fun passResult(result: YouTubeExtraction, audio: AudioInfo) {
        val url = result.streams.filterIsInstance<Stream.AudioStream>().filter {
            listOf(
                Stream.FORMAT_M4A,
                Stream.FORMAT_MPEG_4
            ).any { streamType -> streamType == it.format }
        }.takeIf { it.isNotEmpty() }?.get(0)?.url

        audio.apply {
            audioUri = url!!
            photoUri = result.thumbnails.first().url
            audioTitle = result.title!!
        }

        CoroutineScope(Dispatchers.Main).launch {
            update(audio)
        }
    }

    private suspend fun update(audio: AudioInfo) {
        withContext(Dispatchers.IO) {
            database.update(audio)
        }
    }
}

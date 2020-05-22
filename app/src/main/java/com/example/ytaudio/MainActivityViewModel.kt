package com.example.ytaudio

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.commit451.youtubeextractor.YouTubeExtractor
import com.example.ytaudio.database.AudioDatabaseDao

class MainActivityViewModel(
    val database: AudioDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private val extractor = YouTubeExtractor.Builder().build()

}
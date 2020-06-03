package com.example.ytaudio

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.ytaudio.database.AudioDatabaseDao

class MainActivityViewModel(
    val database: AudioDatabaseDao,
    application: Application
) : AndroidViewModel(application)
package com.example.ytaudio.screens.url_link

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SourceLinkViewModel : ViewModel() {

    private val _link = MutableLiveData<String>()
    val link: LiveData<String>
        get() = _link
}
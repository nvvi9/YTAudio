package com.example.ytaudio.domain

import androidx.lifecycle.LiveData
import com.example.ytaudio.vo.PlaylistItem


interface PlaylistUseCases {

    val playlistItems: LiveData<List<PlaylistItem>>

    suspend fun deleteItems(items: List<PlaylistItem>)
}
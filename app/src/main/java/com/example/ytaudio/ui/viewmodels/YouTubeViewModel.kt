package com.example.ytaudio.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.example.ytaudio.domain.YouTubeUseCases
import com.example.ytaudio.vo.YouTubeItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class YouTubeViewModel @Inject constructor(private val useCases: YouTubeUseCases) : ViewModel() {

    fun getRecommended(): Flow<PagingData<YouTubeItem>> =
        useCases.getRecommendedYouTubeItems()
}
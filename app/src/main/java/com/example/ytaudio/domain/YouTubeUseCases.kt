package com.example.ytaudio.domain

import androidx.paging.PagingData
import com.example.ytaudio.vo.Result
import com.example.ytaudio.vo.YouTubeItem
import kotlinx.coroutines.flow.Flow

interface YouTubeUseCases {

    fun getRecommendedYouTubeItems(): Flow<PagingData<YouTubeItem>>
    suspend fun getYouTubeItemsFromQuery(query: String): Result<List<YouTubeItem>>?
}
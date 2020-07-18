package com.example.ytaudio.domain

import com.example.ytaudio.vo.Result
import com.example.ytaudio.vo.YouTubeItem

interface YouTubeUseCases {

    suspend fun getRecommendedYouTubeItems(): Result<List<YouTubeItem>>?
    suspend fun getYouTubeItemsFromQuery(query: String): Result<List<YouTubeItem>>?
}
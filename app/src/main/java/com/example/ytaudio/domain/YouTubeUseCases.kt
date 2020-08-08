package com.example.ytaudio.domain

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import com.example.ytaudio.repositories.YouTubeRepository
import com.example.ytaudio.vo.YouTubeItem
import com.example.ytaudio.vo.toYouTubeItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
@ExperimentalPagingApi
class YouTubeUseCases @Inject constructor(
    private val repository: YouTubeRepository
) : UseCases {

    fun getRecommendedYouTubeItems(): Flow<PagingData<YouTubeItem>> =
        repository.getVideosResponse().map { data ->
            data.map { it.toYouTubeItem() }
        }

    fun getYouTubeItemsFromQuery(query: String): Flow<PagingData<YouTubeItem>> =
        repository.getSearchResponse(query).map { data -> data.map { it.toYouTubeItem() } }
}
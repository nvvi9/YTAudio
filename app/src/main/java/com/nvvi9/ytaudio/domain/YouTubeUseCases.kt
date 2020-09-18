package com.nvvi9.ytaudio.domain

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.map
import com.nvvi9.ytaudio.repositories.YouTubeRepository
import com.nvvi9.ytaudio.vo.YouTubeItem
import com.nvvi9.ytaudio.vo.toYouTubeItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


@FlowPreview
@ExperimentalCoroutinesApi
@Singleton
@ExperimentalPagingApi
class YouTubeUseCases @Inject constructor(private val repository: YouTubeRepository) : UseCases {

    fun getRecommendedYouTubeItems(): Flow<PagingData<YouTubeItem>> =
        repository.getVideosResponse().map { data ->
            data.map { it.toYouTubeItem() }
        }

    fun getYouTubeItemsFromQuery(query: String): Flow<PagingData<YouTubeItem>> =
        repository.getSearchResponse(query).map { data -> data.map { it.toYouTubeItem() } }
}
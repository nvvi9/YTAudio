package com.nvvi9.ytaudio.domain

import androidx.paging.ExperimentalPagingApi
import androidx.paging.map
import com.nvvi9.ytaudio.repositories.YouTubeRepository
import com.nvvi9.ytaudio.vo.toYouTubeItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


@FlowPreview
@ExperimentalCoroutinesApi
@Singleton
@ExperimentalPagingApi
class YouTubeUseCases @Inject constructor(private val repository: YouTubeRepository) : UseCases {

    fun getRecommendedYouTubeItems() =
        repository.getVideosResponse()
            .map { details ->
                details.map { it.toYouTubeItem() }
            }.flowOn(Dispatchers.IO)

    fun getPopularYouTubeItems() =
        repository.getPopularResponse()
            .map { details ->
                details.map { it.toYouTubeItem() }
            }.flowOn(Dispatchers.IO)

    fun getYouTubeItemsFromQuery(query: String) =
        repository.getSearchResponse(query)
            .map { details ->
                details.map { it.toYouTubeItem() }
            }.flowOn(Dispatchers.IO)
}
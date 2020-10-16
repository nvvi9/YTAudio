package com.nvvi9.ytaudio.domain

import androidx.paging.ExperimentalPagingApi
import androidx.paging.map
import com.nvvi9.ytaudio.repositories.base.YouTubeRepository
import com.nvvi9.ytaudio.vo.toYouTubeItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


@FlowPreview
@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@Singleton
class YouTubeUseCase @Inject constructor(
    private val youTubeRepository: YouTubeRepository
) {

    fun getPopularYouTubeItems() =
        youTubeRepository.getVideoDetails()
            .map { details -> details.map { it.toYouTubeItem() } }

    fun getYouTubeItemsFromQuery(query: String) =
        youTubeRepository.getVideoDetailsFromQuery(query)
            .map { details -> details.map { it.toYouTubeItem() } }
}
package com.example.ytaudio.domain

import androidx.paging.PagingData
import com.example.ytaudio.repositories.base.YouTubeRepository
import com.example.ytaudio.vo.Result
import com.example.ytaudio.vo.Result.Error
import com.example.ytaudio.vo.Result.Success
import com.example.ytaudio.vo.YouTubeItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class YouTubeUseCasesImpl @Inject constructor(
    private val repository: YouTubeRepository
) : YouTubeUseCases {

    override fun getRecommendedYouTubeItems(): Flow<PagingData<YouTubeItem>> =
        repository.getVideosResponse().map { data -> data.map { YouTubeItem.from(it) } }

    override suspend fun getYouTubeItemsFromQuery(query: String): Result<List<YouTubeItem>>? =
        when (val result = repository.getSearchResponse(query)) {
            is Success -> Success(result.data.items.map { YouTubeItem.from(it) })
            is Error -> Error(result.t)
            else -> null
        }
}
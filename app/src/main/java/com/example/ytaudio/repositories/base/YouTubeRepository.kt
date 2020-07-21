package com.example.ytaudio.repositories.base

import androidx.paging.PagingData
import com.example.ytaudio.data.youtube.YTSearchResponse
import com.example.ytaudio.data.youtube.YTVideosItem
import com.example.ytaudio.vo.Result
import kotlinx.coroutines.flow.Flow


interface YouTubeRepository {

    fun getVideosResponse(): Flow<PagingData<YTVideosItem>>
    suspend fun getSearchResponse(query: String): Result<YTSearchResponse>
}

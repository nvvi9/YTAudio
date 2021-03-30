package com.nvvi9.ytaudio.repositories.base

import androidx.paging.PagingData
import com.nvvi9.ytaudio.data.ytstream.YTVideoItems
import kotlinx.coroutines.flow.Flow


interface YouTubeRepository {
    fun getVideoDetailsFromQuery(query: String): Flow<PagingData<YTVideoItems>>
    fun getVideoDetails(): Flow<PagingData<YTVideoItems>>
}
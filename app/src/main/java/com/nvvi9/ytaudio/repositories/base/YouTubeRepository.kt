package com.nvvi9.ytaudio.repositories.base

import androidx.paging.PagingData
import com.nvvi9.ytaudio.data.ytstream.YTData
import kotlinx.coroutines.flow.Flow


interface YouTubeRepository {
    fun getVideoDetailsFromQuery(query: String): Flow<PagingData<YTData>>
    fun getVideoDetails(): Flow<PagingData<YTData.YTVideoDetails>>
}
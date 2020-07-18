package com.example.ytaudio.repositories.base

import com.example.ytaudio.data.youtube.YTSearchResponse
import com.example.ytaudio.data.youtube.YTVideosResponse
import com.example.ytaudio.vo.Result

interface YouTubeRepository {

    suspend fun getVideosResponse(): Result<YTVideosResponse>
    suspend fun getSearchResponse(query: String): Result<YTSearchResponse>
}
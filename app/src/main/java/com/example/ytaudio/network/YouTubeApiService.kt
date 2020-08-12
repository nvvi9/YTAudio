package com.example.ytaudio.network

import com.example.ytaudio.BuildConfig
import com.example.ytaudio.data.youtube.YTSearchPartId
import com.example.ytaudio.data.youtube.YTVideosPartId
import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeApiService {

    @GET("search?key=${BuildConfig.YOUTUBE_API_KEY}&part=id&type=video")
    suspend fun getYTSearchPartId(
        @Query("q") q: String,
        @Query("maxResults") maxResults: Int,
        @Query("pageToken") pageToken: String?
    ): YTSearchPartId

    @GET("videos?key=${BuildConfig.YOUTUBE_API_KEY}&part=id&chart=mostPopular&videoCategoryId=28")
    suspend fun getYTVideosIdResponse(
        @Query("maxResults") maxResults: Int,
        @Query("pageToken") pageToken: String? = null
    ): YTVideosPartId
}
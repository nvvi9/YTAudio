package com.example.ytaudio.network

import com.example.ytaudio.BuildConfig
import com.example.ytaudio.data.youtube.YTSearchPartId
import com.example.ytaudio.data.youtube.YTSearchResponse
import com.example.ytaudio.data.youtube.YTVideosPartId
import com.example.ytaudio.data.youtube.YTVideosResponse
import retrofit2.http.GET
import retrofit2.http.Query

private const val API_KEY = BuildConfig.YOUTUBE_API_KEY

interface YouTubeApiService {

    @GET("search?key=$API_KEY&part=snippet&type=video")
    suspend fun getYTSearchResponse(
        @Query("q") q: String,
        @Query("maxResults") maxResults: Int = 25
    ): YTSearchResponse

    @GET("search?key=$API_KEY&part=id&type=video")
    suspend fun getYTSearchPartId(
        @Query("q") q: String,
        @Query("maxResults") maxResults: Int,
        @Query("pageToken") pageToken: String?
    ): YTSearchPartId

    @GET("videos?key=$API_KEY&part=snippet&chart=mostPopular&videoCategoryId=10")
    suspend fun getYTVideosResponse(
        @Query("maxResults") maxResults: Int,
        @Query("pageToken") pageToken: String? = null
    ): YTVideosResponse

    @GET("videos?key=$API_KEY&part=id&chart=mostPopular&videoCategoryId=10")
    suspend fun getYTVideosIdResponse(
        @Query("maxResults") maxResults: Int,
        @Query("pageToken") pageToken: String? = null
    ): YTVideosPartId
}
package com.example.ytaudio.network

import com.example.ytaudio.data.streamyt.VideoData
import com.example.ytaudio.data.streamyt.VideoDetails
import retrofit2.http.GET
import retrofit2.http.Query


interface YTStreamApiService {

    @GET("videodata")
    suspend fun getVideoData(@Query("id") id: String): List<VideoData>

    @GET("videodetails")
    suspend fun getVideoDetails(@Query("id") id: String): List<VideoDetails>
}
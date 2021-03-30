package com.nvvi9.ytaudio.network

import com.nvvi9.ytaudio.data.datatype.tryCatching
import com.nvvi9.ytaudio.network.retrofit.YouTubeApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject


@FlowPreview
@ExperimentalCoroutinesApi
class YouTubeNetworkDataSource @Inject constructor(private val youTubeApiService: YouTubeApiService) {

//    suspend fun getFromQuery(query: String, maxResults: Int, pageToken: String?) =
//            tryCatching { youTubeApiService.getYTSearchPartId(query, maxResults, pageToken) }

    suspend fun getPopular(maxResults: Int, pageToken: String?) =
            tryCatching { youTubeApiService.getYTVideosIdResponse(maxResults, pageToken) }

    suspend fun getPlaylistItems(playlistId: String, maxResults: Int, pageToken: String?) =
            tryCatching { youTubeApiService.getYTPlaylistItemsPartSnippet(playlistId, maxResults, pageToken) }

    suspend fun getFromQuerySnippet(query: String, maxResults: Int, pageToken: String?) =
            tryCatching { youTubeApiService.getYTSearchPartSnippet(query, maxResults, pageToken) }
}
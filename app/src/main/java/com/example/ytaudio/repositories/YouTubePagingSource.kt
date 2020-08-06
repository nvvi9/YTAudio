package com.example.ytaudio.repositories

import androidx.paging.PagingSource
import com.example.ytaudio.data.streamyt.VideoDetails
import com.example.ytaudio.network.YTStreamApiService
import com.example.ytaudio.network.YouTubeApiService
import kotlinx.coroutines.FlowPreview
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


class YouTubePagingSource @Inject constructor(
    private val ytApiService: YouTubeApiService,
    private val ytStreamApiService: YTStreamApiService
) : PagingSource<String, VideoDetails>() {

    @FlowPreview
    override suspend fun load(params: LoadParams<String>): LoadResult<String, VideoDetails> {
        return try {
            val ytVideosPartId = ytApiService.getYTVideosIdResponse(params.loadSize, params.key)

            val items =
                ytStreamApiService.getVideoDetails(ytVideosPartId.items.joinToString("+") { it.id })

            LoadResult.Page(
                data = items,
                prevKey = ytVideosPartId.prevPageToken,
                nextKey = ytVideosPartId.nextPageToken
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}
package com.example.ytaudio.repositories

import androidx.paging.PagingSource
import com.example.ytaudio.data.streamyt.VideoDetails
import com.example.ytaudio.network.YTStreamApiService
import com.example.ytaudio.network.YouTubeApiService
import kotlinx.coroutines.FlowPreview
import retrofit2.HttpException
import java.io.IOException


class YTSearchPagingSource(
    private val query: String,
    private val ytApiService: YouTubeApiService,
    private val ytStreamApiService: YTStreamApiService
) : PagingSource<String, VideoDetails>() {


    @FlowPreview
    override suspend fun load(params: LoadParams<String>): LoadResult<String, VideoDetails> =
        try {
            val ytSearchPartId = ytApiService.getYTSearchPartId(query, params.loadSize, params.key)

//            val items = ytSearchPartId.items.asFlow()
//                .flatMapMerge { ytExtractor.extractVideoDataFlow(it.id.videoId) }
//                .flowOn(Dispatchers.IO)
//                .filterNotNull()
//                .toList()

            val items =
                ytStreamApiService.getVideoDetails(ytSearchPartId.items.joinToString("+") { it.id.videoId })

            LoadResult.Page(
                data = items,
                prevKey = ytSearchPartId.prevPageToken,
                nextKey = ytSearchPartId.nextPageToken
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
}

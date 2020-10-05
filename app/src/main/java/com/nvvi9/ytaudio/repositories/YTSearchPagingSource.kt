package com.nvvi9.ytaudio.repositories

import androidx.paging.PagingSource
import com.nvvi9.YTStream
import com.nvvi9.ytaudio.data.ytstream.YTVideoDetails
import com.nvvi9.ytaudio.network.YouTubeApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.toList
import retrofit2.HttpException
import java.io.IOException


@ExperimentalCoroutinesApi
@FlowPreview
class YTSearchPagingSource(
    private val query: String,
    private val ytApiService: YouTubeApiService,
    private val ytStream: YTStream,
) : PagingSource<String, YTVideoDetails>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, YTVideoDetails> =
        try {
            val ytSearch = ytApiService.getYTSearchPartId(query, params.loadSize, params.key)

            val items =
                ytStream.extractVideoDetails(*ytSearch.items.map { it.id.videoId }.toTypedArray())
                    .toList()
                    .filterNotNull()
                    .map { YTVideoDetails.create(it) }

            LoadResult.Page(
                data = items,
                prevKey = ytSearch.prevPageToken,
                nextKey = ytSearch.nextPageToken
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
}

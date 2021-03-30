package com.nvvi9.ytaudio.repositories.paging

import android.util.Log
import androidx.paging.PagingSource
import com.nvvi9.ytaudio.data.datatype.Result
import com.nvvi9.ytaudio.data.youtube.Item
import com.nvvi9.ytaudio.data.ytstream.YTVideoItems
import com.nvvi9.ytaudio.network.YTStreamDataSource
import com.nvvi9.ytaudio.network.YouTubeNetworkDataSource
import com.nvvi9.ytaudio.repositories.mapper.YTPlaylistMapper
import com.nvvi9.ytaudio.repositories.mapper.YTVideoMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList


@ExperimentalCoroutinesApi
@FlowPreview
class YTSearchPagingSource(
        private val query: String,
        private val ytNetworkDataSource: YouTubeNetworkDataSource,
        private val ytStreamDataSource: YTStreamDataSource
) : PagingSource<String, YTVideoItems>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, YTVideoItems> =
            ytNetworkDataSource.getFromQuerySnippet(query, params.loadSize, params.key).let { result ->
                when (result) {
                    is Result.Success -> LoadResult.Page(getYTVideoItems(result.data.items), result.data.prevPageToken, result.data.nextPageToken)
                    is Result.Error -> LoadResult.Error(result.throwable.also { Log.e(this::class.simpleName, it.stackTraceToString()) })
                }
            }


    private suspend fun getYTVideoItems(items: List<Item>): List<YTVideoItems> = coroutineScope {
        async {
            ytStreamDataSource.extractVideoDetails(items.mapNotNull { it.id.videoId })
                    .filterNotNull()
                    .map { YTVideoMapper.map(it) }
                    .toList()
        }.let {
            items.mapNotNull { item ->
                item.id.playlistId?.let {
                    YTPlaylistMapper.map(item)
                }
            } + it.await()
        }
    }
}
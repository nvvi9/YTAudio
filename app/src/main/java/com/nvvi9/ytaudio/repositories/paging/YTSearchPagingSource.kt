package com.nvvi9.ytaudio.repositories.paging

import androidx.paging.PagingSource
import com.nvvi9.ytaudio.data.datatype.Result
import com.nvvi9.ytaudio.data.ytstream.YTData
import com.nvvi9.ytaudio.network.YTStreamDataSource
import com.nvvi9.ytaudio.network.YouTubeNetworkDataSource
import com.nvvi9.ytaudio.repositories.mapper.YTVideoDetailsMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList


@ExperimentalCoroutinesApi
@FlowPreview
class YTSearchPagingSource(
    private val query: String,
    private val ytNetworkDataSource: YouTubeNetworkDataSource,
    private val ytStreamDataSource: YTStreamDataSource
) : PagingSource<String, YTData>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, YTData> {
        ytNetworkDataSource.getFromQuery(query, params.loadSize, params.key).run {
            when (this) {
                is Result.Success -> {
                    val result = mutableListOf<YTData>()

                    val videoDetails =
                        getYTVideoDetails(data.items.mapNotNull { it.id.videoId })

                    val videoPlaylist = data.items.mapNotNull { it.id.playlistId }
                }
            }
        }
    }

    private suspend fun getYTVideoDetails(id: List<String>) =
        ytStreamDataSource.extractVideoDetails(id)
            .filterNotNull()
            .map { YTVideoDetailsMapper.map(it) }
            .toList()


    //    override suspend fun load(params: LoadParams<String>): LoadResult<String, YTData> =
//        ytNetworkDataSource.getFromQuery(query, params.loadSize, params.key).run {
//            when (this) {
//                is Result.Success -> {
//                    ytStreamDataSource.extractVideoDetails(data.items.map { it.id.videoId })
//                        .filterNotNull()
//                        .map { YTVideoDetailsMapper.map(it) }
//                        .toList()
//                        .let { LoadResult.Page(it, data.prevPageToken, data.nextPageToken) }
//                }
//                is Result.Error -> LoadResult.Error(throwable)
//            }
//        }
}
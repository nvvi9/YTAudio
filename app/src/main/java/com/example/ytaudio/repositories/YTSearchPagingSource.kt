package com.example.ytaudio.repositories

import androidx.paging.PagingSource
import com.example.ytaudio.data.audioinfo.AudioInfo
import com.example.ytaudio.network.YTExtractor
import com.example.ytaudio.network.YouTubeApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import java.io.IOException


class YTSearchPagingSource(
    private val query: String,
    private val ytApiService: YouTubeApiService,
    private val ytExtractor: YTExtractor
) : PagingSource<String, AudioInfo>() {


    @FlowPreview
    override suspend fun load(params: LoadParams<String>): LoadResult<String, AudioInfo> =
        try {
            val ytSearchPartId = ytApiService.getYTSearchPartId(query, params.loadSize, params.key)

            val items = ytSearchPartId.items.asFlow()
                .flatMapMerge { ytExtractor.extractAudioInfoFlow(it.id.videoId) }
                .flowOn(Dispatchers.IO)
                .filterNotNull()
                .toList()

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

package com.example.ytaudio.repositories

import androidx.paging.PagingSource
import com.example.ytaudio.data.audioinfo.AudioInfo
import com.example.ytaudio.network.YTExtractor
import com.example.ytaudio.network.YouTubeApiService
import com.example.ytaudio.utils.extensions.mapParallel
import com.github.kotvertolet.youtubejextractor.exception.ExtractionException
import com.github.kotvertolet.youtubejextractor.exception.YoutubeRequestException
import kotlinx.coroutines.Dispatchers
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


class YouTubePagingSource @Inject constructor(
    private val ytApiService: YouTubeApiService,
    private val ytExtractor: YTExtractor
) : PagingSource<String, AudioInfo>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, AudioInfo> {
        return try {
            val ytVideosPartId = ytApiService.getYTVideosIdResponse(params.loadSize, params.key)
            val items = ytVideosPartId.items
                .mapParallel(Dispatchers.IO) { ytExtractor.extractAudioInfo(it.id) }.filterNotNull()
            LoadResult.Page(
                data = items,
                prevKey = ytVideosPartId.prevPageToken,
                nextKey = ytVideosPartId.nextPageToken
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        } catch (e: ExtractionException) {
            LoadResult.Error(e)
        } catch (e: YoutubeRequestException) {
            LoadResult.Error(e)
        }
    }
}
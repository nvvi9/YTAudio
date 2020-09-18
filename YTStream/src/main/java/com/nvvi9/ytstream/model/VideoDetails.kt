package com.nvvi9.ytstream.model

import com.nvvi9.ytstream.model.raw.RawResponse
import com.nvvi9.ytstream.utils.ifNotNull
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow


data class VideoDetails(
    val id: String,
    val title: String,
    val channel: String?,
    val channelId: String?,
    val description: String?,
    val durationSeconds: Long?,
    val viewCount: Long?,
    val thumbnails: List<Thumbnail>,
    val expiresInSeconds: Long?,
    val isLiveStream: Boolean?,
    internal val isSignatureEncoded: Boolean,
    internal val statusOk: Boolean,
    internal val rawResponse: RawResponse
) {

    override fun toString(): String =
        "VideoDetails(id=$id, title=$title, channel=$channel, channelId=$channelId," +
                " durationSeconds=$durationSeconds"

    companion object {

        internal suspend fun fromId(id: String) = coroutineScope {
            val raw = RawResponse.fromId(id)
            val thumbnailUrl = "https://img.youtube.com/vi/$id"
            val thumbnails = listOf(
                Thumbnail(120, 90, "$thumbnailUrl/default.jpg"),
                Thumbnail(320, 180, "$thumbnailUrl/mqdefault.jpg"),
                Thumbnail(480, 360, "$thumbnailUrl/hqdefault.jpg")
            )

            raw?.run {
                ifNotNull(id, title) { id, title ->
                    VideoDetails(
                        id, title, author, channelId, description, durationSeconds, viewCount,
                        thumbnails, expiresInSeconds, isLiveStream, isEncoded, statusOk, this
                    )
                }
            }
        }

        internal suspend fun fromIdFlow(id: String) = flow {
            emit(fromId(id))
        }
    }
}

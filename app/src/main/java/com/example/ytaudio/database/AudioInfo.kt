package com.example.ytaudio.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.ytaudio.domain.PlaylistItem
import com.example.ytaudio.network.extractor.YTExtractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Entity(
    tableName = "audio_playlist_table",
    indices = [Index(value = ["youtube_id"], unique = true)]
)
data class AudioInfo(

    @ColumnInfo(name = "youtube_id") @PrimaryKey val youtubeId: String = "",
    @ColumnInfo(name = "streaming_uri") var audioStreamingUri: String = "",
    @ColumnInfo(name = "thumbnail_uri") var thumbnailUri: String = "",
    @ColumnInfo(name = "title") var title: String = "",
    @ColumnInfo(name = "author") var author: String = "",
    @ColumnInfo(name = "author_id") var authorId: String = "",
    @ColumnInfo(name = "description") var description: String = "",
    @ColumnInfo(name = "keywords") var keywords: String = "",
    @ColumnInfo(name = "view_count") var viewCount: Int = 0,
    @ColumnInfo(name = "average_rating") var averageRating: Double = 0.0,
    @ColumnInfo(name = "audio_format") var audioFormat: String = "",
    @ColumnInfo(name = "codec") var codec: String = "",
    @ColumnInfo(name = "bitrate") var bitrate: Int = 0,
    @ColumnInfo(name = "average_bitrate") var averageBitrate: Int = 0,
    @ColumnInfo(name = "audio_duration_seconds") var audioDurationSeconds: Long = 0L,
    @ColumnInfo(name = "last_update_time_seconds") var lastUpdateTimeSeconds: Long = 0L,
    @ColumnInfo(name = "streaming_uri_active_time_seconds") var streamingUriActiveTimeSeconds: Long = 0L
) {

    val needUpdate: Boolean
        get() = System.currentTimeMillis() >= (lastUpdateTimeSeconds + streamingUriActiveTimeSeconds - 10) * 1000

    fun toPlaylistItem() =
        PlaylistItem(
            id = youtubeId,
            title = title,
            author = author,
            thumbnailUri = thumbnailUri,
            duration = audioDurationSeconds
        )

    suspend fun update() {
        withContext(Dispatchers.Default) {
            YTExtractor().extractAudioInfo(youtubeId).let {
                audioStreamingUri = it.audioStreamingUri
                thumbnailUri = it.thumbnailUri
                title = it.title
                author = it.author
                authorId = it.authorId
                description = it.description
                keywords = it.keywords
                viewCount = it.viewCount
                averageRating = it.averageRating
                audioFormat = it.audioFormat
                codec = it.codec
                bitrate = it.bitrate
                averageBitrate = it.averageBitrate
                audioDurationSeconds = it.audioDurationSeconds
                lastUpdateTimeSeconds = it.lastUpdateTimeSeconds
                streamingUriActiveTimeSeconds = it.streamingUriActiveTimeSeconds
            }
        }
    }

    override fun toString() =
        "ID: $authorId\n" +
                "YouTube ID: $youtubeId\n" +
                "Audio URI: $audioStreamingUri\n" +
                "Thumbnail URI: $thumbnailUri\n" +
                "Audio title: $title\n" +
                "Author: $author\n" +
                "Author ID: $authorId\n" +
                "Description: $description\n" +
                "Keywords: $keywords\n" +
                "Number of views: $viewCount\n" +
                "Average rating: $averageRating\n" +
                "Audio format: $audioFormat\n" +
                "Codec: $codec\n" +
                "Bitrate: $bitrate\n" +
                "Average bitrate: $averageBitrate\n" +
                "Duration (seconds): $audioDurationSeconds\n" +
                "Last update (seconds): $lastUpdateTimeSeconds\n" +
                "Active URI time (seconds): $streamingUriActiveTimeSeconds\n"
}


fun List<AudioInfo>.asPlaylistItems() =
    map { it.toPlaylistItem() }
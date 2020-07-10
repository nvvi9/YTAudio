package com.example.ytaudio.database.entities

import com.github.kotvertolet.youtubejextractor.models.youtube.videoData.VideoDetails

data class AudioDetails(
    val title: String,
    val author: String,
    val channelId: String,
    val description: String,
    val keywords: List<String>,
    val durationSeconds: Int,
    var averageRating: Double,
    var viewCount: String
) {

    companion object {

        fun from(videoDetails: VideoDetails) =
            videoDetails.run {
                AudioDetails(
                    title, author, channelId, shortDescription,
                    keywords, lengthSeconds.toIntOrNull() ?: 0,
                    averageRating, viewCount
                )
            }
    }
}
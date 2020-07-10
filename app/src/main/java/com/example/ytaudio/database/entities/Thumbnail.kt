package com.example.ytaudio.database.entities

import com.github.kotvertolet.youtubejextractor.models.youtube.videoData.ThumbnailsItem

data class Thumbnail(
    val uri: String,
    val width: Int,
    val height: Int
) {

    companion object {

        fun from(thumbnail: ThumbnailsItem) =
            thumbnail.run {
                Thumbnail(url, width, height)
            }
    }
}
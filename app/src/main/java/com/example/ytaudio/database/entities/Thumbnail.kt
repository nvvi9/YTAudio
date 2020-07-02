package com.example.ytaudio.database.entities

import com.github.kotvertolet.youtubejextractor.models.youtube.videoData.ThumbnailsItem

data class Thumbnail(
    val uri: String,
    val width: Int,
    val height: Int
) {

    constructor(thumbnail: ThumbnailsItem) :
            this(thumbnail.url, thumbnail.width, thumbnail.height)
}
package com.nvvi9.ytaudio.data.audioinfo

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nvvi9.model.Thumbnail


@Entity(indices = [Index(value = ["id"], unique = true)])
data class AudioInfo(
    @PrimaryKey val id: String,
    @Embedded val details: AudioDetails,
    val thumbnails: List<Thumbnail>,
    val audioStreams: List<AudioStream>,
    val expiresInSeconds: Long?,
    val lastUpdateTimeSeconds: Long
) {

    val needUpdate
        get() = expiresInSeconds?.let {
            System.currentTimeMillis() / 1000 >= lastUpdateTimeSeconds + it - 10
        }
}
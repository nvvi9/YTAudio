package com.example.ytaudio.data.audioinfo

import androidx.room.*
import com.example.ytaudio.data.streamyt.Thumbnail


@Entity(indices = [Index(value = ["id"], unique = true)])
data class AudioInfo(
    @PrimaryKey val id: String,
    @Embedded val details: AudioDetails,
    val thumbnails: List<Thumbnail>,
    val audioStreams: List<AudioStream>,
    val aliveTimeMillis: Long,
    val lastUpdateTimeMillis: Long
) {

    val needUpdate: Boolean
        get() =
            System.currentTimeMillis() >= lastUpdateTimeMillis + aliveTimeMillis - UPDATE_TIME_GAP


    companion object {
        @Ignore
        private const val UPDATE_TIME_GAP = 10
    }
}

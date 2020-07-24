package com.example.ytaudio.data.audioinfo

import androidx.room.*
import com.example.ytaudio.data.videodata.*


@Entity(indices = [Index(value = ["id"], unique = true)])
data class AudioInfo(
    @PrimaryKey override val id: String,
    @Embedded override val details: Details,
    override val thumbnails: List<Thumbnail>,
    override val audioStreams: List<AudioStream>,
    override val aliveTimeMillis: Long,
    override val lastUpdateTimeMillis: Long
) : YTData {

    val needUpdate: Boolean
        get() =
            System.currentTimeMillis() >= lastUpdateTimeMillis + aliveTimeMillis - UPDATE_TIME_GAP


    companion object {
        @Ignore
        private const val UPDATE_TIME_GAP = 10
    }
}

fun VideoData.toAudioInfo() =
    AudioInfo(id, details, thumbnails, audioStreams, aliveTimeMillis, lastUpdateTimeMillis)
package com.example.ytaudio.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "audio_playlist_table",
    indices = [Index(value = ["youtube_id"], unique = true)]
)
data class AudioInfo(

    @PrimaryKey(autoGenerate = true)
    var audioId: Long = 0L,

    @ColumnInfo(name = "youtube_id")
    val youtubeId: String,

    @ColumnInfo(name = "audio_uri")
    var audioUri: String,

    @ColumnInfo(name = "photo_uri")
    var photoUri: String,

    @ColumnInfo(name = "audio_title")
    var audioTitle: String

)
package com.example.ytaudio.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audio_playlist_table")
data class AudioInfo(

    @PrimaryKey(autoGenerate = true)
    var audioId: Long = 0L,

    @ColumnInfo(name = "audio_uri")
    val audioUri: String,

    @ColumnInfo(name = "photo_uri")
    val photoUri: String,

    @ColumnInfo(name = "audio_title")
    val audioTitle: String

    )
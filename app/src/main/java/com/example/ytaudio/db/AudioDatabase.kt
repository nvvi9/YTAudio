package com.example.ytaudio.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.ytaudio.data.audioinfo.AudioInfo
import com.example.ytaudio.data.streamyt.VideoDetails
import com.example.ytaudio.data.streamyt.VideoDetailsRemoteKeys


@Database(
    entities = [
        AudioInfo::class,
        VideoDetails::class,
        VideoDetailsRemoteKeys::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AudioDatabase : RoomDatabase() {

    abstract val audioInfoDao: AudioInfoDao
    abstract val videoDetailsDao: VideoDetailsDao
    abstract val videoDetailsRemoteKeysDao: VideoDetailsRemoteKeysDao
}
package com.nvvi9.ytaudio.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nvvi9.ytaudio.data.audioinfo.AudioInfo
import com.nvvi9.ytaudio.data.ytstream.YTVideoDetails
import com.nvvi9.ytaudio.data.ytstream.YTVideoDetailsRemoteKeys


@Database(
    entities = [
        AudioInfo::class,
        YTVideoDetails::class,
        YTVideoDetailsRemoteKeys::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AudioDatabase : RoomDatabase() {

    abstract val audioInfoDao: AudioInfoDao
    abstract val ytVideoDetailsDao: YTVideoDetailsDao
    abstract val ytVideoDetailsRemoteKeysDao: YTVideoDetailsRemoteKeysDao
}
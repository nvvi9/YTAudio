package com.example.ytaudio.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.ytaudio.data.audioinfo.AudioInfo
import com.example.ytaudio.data.youtube.YTRemoteKeys
import com.example.ytaudio.data.youtube.YTVideosItem


@Database(
    entities = [AudioInfo::class, YTRemoteKeys::class, YTVideosItem::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AudioDatabase : RoomDatabase() {

    abstract val playlistDao: PlaylistDao
    abstract val ytRemoteKeysDao: YTRemoteKeysDao
    abstract val ytVideosItemDao: YTVideosItemDao
}
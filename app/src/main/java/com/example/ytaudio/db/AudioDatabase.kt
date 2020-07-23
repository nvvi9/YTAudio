package com.example.ytaudio.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.ytaudio.data.audioinfo.AudioInfo
import com.example.ytaudio.data.youtube.YTVideosItem
import com.example.ytaudio.data.youtube.YTVideosRemoteKeys


@Database(
    entities = [AudioInfo::class, YTVideosRemoteKeys::class, YTVideosItem::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AudioDatabase : RoomDatabase() {

    abstract val playlistDao: PlaylistDao
    abstract val ytVideosRemoteKeysDao: YTVideosRemoteKeysDao
    abstract val ytVideosItemDao: YTVideosItemDao
}
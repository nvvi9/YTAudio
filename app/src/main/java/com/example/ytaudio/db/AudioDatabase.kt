package com.example.ytaudio.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.ytaudio.data.audioinfo.AudioInfo
import com.example.ytaudio.data.videodata.VideoData
import com.example.ytaudio.data.videodata.VideoDataRemoteKeys
import com.example.ytaudio.data.youtube.YTVideosItem
import com.example.ytaudio.data.youtube.YTVideosRemoteKeys


@Database(
    entities = [AudioInfo::class, VideoData::class, YTVideosItem::class, YTVideosRemoteKeys::class, VideoDataRemoteKeys::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AudioDatabase : RoomDatabase() {

    abstract val playlistDao: PlaylistDao
    abstract val ytVideosItemDao: YTVideosItemDao
    abstract val ytVideosRemoteKeysDao: YTVideosRemoteKeysDao
    abstract val videoDataRemoteKeysDao: VideoDataRemoteKeysDao
    abstract val videoDataDao: VideoDataDao
}
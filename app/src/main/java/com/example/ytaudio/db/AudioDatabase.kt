package com.example.ytaudio.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.ytaudio.data.audioinfo.AudioInfo
import com.example.ytaudio.data.streamyt.VideoDetails
import com.example.ytaudio.data.streamyt.VideoDetailsRemoteKeys
import com.example.ytaudio.data.videodata.VideoData
import com.example.ytaudio.data.videodata.VideoDataRemoteKeys


@Database(
    entities = [
        AudioInfo::class,
        VideoData::class,
        VideoDataRemoteKeys::class,
        VideoDetails::class,
        VideoDetailsRemoteKeys::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AudioDatabase : RoomDatabase() {

    abstract val playlistDao: PlaylistDao
    abstract val videoDataRemoteKeysDao: VideoDataRemoteKeysDao
    abstract val videoDataDao: VideoDataDao
    abstract val videoDetailsDao: VideoDetailsDao
    abstract val videoDetailsRemoteKeysDao: VideoDetailsRemoteKeysDao
}
package com.example.ytaudio.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.ytaudio.data.audioinfo.AudioInfo

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(audio: AudioInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(audioList: List<AudioInfo>)

    @Update
    suspend fun update(audio: AudioInfo)

    @Update
    suspend fun update(audioList: List<AudioInfo>)

    @Query("SELECT * FROM AudioInfo ORDER BY title")
    suspend fun getAllAudioInfo(): List<AudioInfo>

    @Query("SELECT * FROM AudioInfo ORDER BY title")
    fun getAllAudio(): LiveData<List<AudioInfo>>

    @Delete
    suspend fun delete(audio: AudioInfo)

    @Query("DELETE FROM AudioInfo WHERE youtubeId IN (:idList)")
    suspend fun delete(idList: List<String>)

    @Query("DELETE FROM AudioInfo")
    suspend fun clear()
}
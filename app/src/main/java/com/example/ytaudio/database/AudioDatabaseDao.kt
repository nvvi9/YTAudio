package com.example.ytaudio.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface AudioDatabaseDao {
    @Insert
    fun insert(audio: AudioInfo)

    @Update
    fun update(audio: AudioInfo)

    @Update
    fun update(audioList: List<AudioInfo>)

    @Query("SELECT * FROM audio_playlist_table WHERE audioId = :key")
    fun get(key: Long): AudioInfo?

    @Query("DELETE FROM audio_playlist_table")
    fun clear()

    @Query("SELECT * FROM audio_playlist_table ORDER BY audioId DESC")
    fun getAllAudioInfo(): List<AudioInfo>

    @Query("SELECT * FROM audio_playlist_table ORDER BY audioId DESC")
    fun getAllAudio(): LiveData<List<AudioInfo>?>

    @Query("SELECT * FROM audio_playlist_table ORDER BY audioId DESC LIMIT 1")
    fun getLastAudio(): AudioInfo?
}
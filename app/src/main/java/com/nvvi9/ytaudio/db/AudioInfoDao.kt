package com.nvvi9.ytaudio.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nvvi9.ytaudio.data.audioinfo.AudioInfo

@Dao
interface AudioInfoDao : BaseDao<AudioInfo> {

    @Transaction
    suspend fun updatePlaylist(items: List<AudioInfo>) {
        val oldData = getAllAudioInfo()
        update(items)
        delete(oldData - items)
    }

    @Update
    suspend fun update(audioList: List<AudioInfo>)

    @Query("SELECT * FROM AudioInfo ORDER BY title")
    suspend fun getAllAudioInfo(): List<AudioInfo>

    @Query("SELECT * FROM AudioInfo ORDER BY title")
    fun getAllAudio(): LiveData<List<AudioInfo>>

    @Delete
    suspend fun delete(items: List<AudioInfo>)

    @Query("DELETE FROM AudioInfo WHERE id IN (:id)")
    suspend fun deleteById(vararg id: String)
}
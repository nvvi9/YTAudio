package com.example.ytaudio.database

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ytaudio.network.extractor.YTExtractor
import com.example.ytaudio.utils.extensions.mapParallel
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class DatabaseExtractInsertTest {

    private lateinit var databaseDao: AudioDatabaseDao
    private lateinit var database: AudioDatabase

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AudioDatabase::class.java).build()
        databaseDao = database.audioDatabaseDao
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun testExtractInsert() {
        runBlocking {
            val idList =
                listOf("qkKT-0FWyfs", "1F5WLJ7Ni9c", "lkFkuC9aqS4", "3yOlBB-B32E", "WkIHxF4dPDk")

            val audioInfoList =
                idList.mapParallel(Dispatchers.IO) {
                    YTExtractor().extractAudioInfo(it)
                }

            databaseDao.insert(audioInfoList)

            val databaseAudioInfo = databaseDao.getAllAudioInfo()

            idList.forEach { id ->
                assertTrue(id in databaseAudioInfo.map { it.youtubeId })
            }
        }
    }
}
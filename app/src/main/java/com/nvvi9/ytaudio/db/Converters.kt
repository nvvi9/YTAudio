package com.nvvi9.ytaudio.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nvvi9.model.Thumbnail
import com.nvvi9.ytaudio.data.audioinfo.AudioStream


class Converters {

    @TypeConverter
    fun toThumbnailList(value: String): List<Thumbnail> {
        val type = object : TypeToken<List<Thumbnail>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromThumbnailList(list: List<Thumbnail>): String =
        Gson().toJson(list)

    @TypeConverter
    fun toAudioStream(value: String): List<AudioStream> {
        val type = object : TypeToken<List<AudioStream>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromAudioStream(audioStream: List<AudioStream>): String =
        Gson().toJson(audioStream)
}
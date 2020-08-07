package com.example.ytaudio.db

import androidx.room.TypeConverter
import com.example.ytaudio.data.audioinfo.AudioStream
import com.example.ytaudio.data.streamyt.Thumbnail
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class Converters {

    @TypeConverter
    fun toThumbnail(value: String): Thumbnail =
        Gson().fromJson(value, Thumbnail::class.java)

    @TypeConverter
    fun fromThumbnail(thumbnail: Thumbnail): String =
        Gson().toJson(thumbnail)

    @TypeConverter
    fun toThumbnailList(value: String): List<Thumbnail> {
        val type = object : TypeToken<List<Thumbnail>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromThumbnailList(list: List<Thumbnail>): String =
        Gson().toJson(list)

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        val type = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromStringList(list: List<String>?): String? =
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
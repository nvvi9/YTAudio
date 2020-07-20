package com.example.ytaudio.db

import androidx.room.TypeConverter
import com.example.ytaudio.data.audioinfo.AudioStream
import com.example.ytaudio.data.audioinfo.Thumbnail
import com.example.ytaudio.data.youtube.YTVideosItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun toAudioStreamList(value: String): List<AudioStream> {
        val type = object : TypeToken<List<AudioStream>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromAudioStreamList(list: List<AudioStream>): String =
        Gson().toJson(list)

    @TypeConverter
    fun toThumbnailList(value: String): List<Thumbnail> {
        val type = object : TypeToken<List<Thumbnail>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromThumbnailList(list: List<Thumbnail>): String =
        Gson().toJson(list)

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromStringList(list: List<String>): String =
        Gson().toJson(list)

    @TypeConverter
    fun toYTVideosItemList(value: String): List<YTVideosItem> {
        val type = object : TypeToken<List<YTVideosItem>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromYTVideosItemList(list: List<YTVideosItem>) =
        Gson().toJson(list)
}
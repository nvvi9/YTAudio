package com.example.ytaudio.database

import androidx.room.TypeConverter
import com.example.ytaudio.database.entities.AudioStream
import com.example.ytaudio.database.entities.Thumbnail
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converter {

    @TypeConverter
    fun toAudioStreamList(value: String): List<AudioStream> {
        val type = object : TypeToken<List<AudioStream>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromAudioStreamList(list: List<AudioStream>): String {
        val gson = Gson()
        return gson.toJson(list)
    }

    @TypeConverter
    fun toThumbnailList(value: String): List<Thumbnail> {
        val type = object : TypeToken<List<Thumbnail>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromThumbnailList(list: List<Thumbnail>): String {
        val gson = Gson()
        return gson.toJson(list)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> =
        value.split(", ")

    @TypeConverter
    fun fromStringList(list: List<String>): String =
        list.joinToString()
}
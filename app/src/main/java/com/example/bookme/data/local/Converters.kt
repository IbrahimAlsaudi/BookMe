package com.example.bookme.data.local

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromStringList(list: List<String>): String = json.encodeToString(list)

    @TypeConverter
    fun toStringList(data: String): List<String> =
        runCatching { json.decodeFromString<List<String>>(data) }.getOrDefault(emptyList())
}
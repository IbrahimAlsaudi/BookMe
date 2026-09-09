package com.example.bookme.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.bookme.data.local.Converters

@Entity(tableName = "hotels")
@TypeConverters(Converters::class)
data class HotelEntity(
    @PrimaryKey val id: String,
    val name: String,
    val city: String,
    val country: String,
    val rating: Double,
    val pricePerNight: Double,
    val currency: String,
    val imageUrl: String,
    val images: List<String>,
    val amenities: List<String>,
    val description: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val isFavorite: Boolean = false,
    val cachedAt: Long,
)
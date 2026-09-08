package com.example.bookme.domain.model

data class Hotel(
    val id: Long,
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
    val latitude: Double,
    val longitude: Double,
    val isFavorite: Boolean = false
)

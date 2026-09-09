package com.example.bookme.domain.model.hotel

data class Hotel(
    val id: String,
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
    val isFavorite: Boolean = false
)
package com.example.bookme.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class HotelDto(
    val id: String = "",
    val name: String = "",
    val city: String = "",
    val country: String = "",
    val rating: Double = 0.0,
    val pricePerNight: Double = 0.0,
    val currency: String = "USD",
    val imageUrl: String = "",
    val images: List<String> = emptyList(),
    val amenities: List<String> = emptyList(),
    val description: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
)


package com.example.bookme.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object HotelList : Screen

    @Serializable
    data object Favorites : Screen

    @Serializable
    data class HotelDetail(val hotelId: String) : Screen

    @Serializable
    data class Booking(val hotelId: String) : Screen
}

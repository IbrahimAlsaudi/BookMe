package com.example.bookme.domain.model.hotel

data class HotelFilters(
    val city: String? = null,
    val minRating: Double? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null
) {
    val isActive: Boolean
        get() = city != null || minRating != null || minPrice != null || maxPrice != null
}
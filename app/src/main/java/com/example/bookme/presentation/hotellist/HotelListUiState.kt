package com.example.bookme.presentation.hotellist

import com.example.bookme.domain.error.AppError
import com.example.bookme.domain.model.hotel.Hotel
import com.example.bookme.domain.model.hotel.HotelFilters

data class HotelListUiState(
    val hotels: List<Hotel> = emptyList(),
    val searchText: String = "",
    val filter: HotelFilters = HotelFilters(),
    val isLoading: Boolean = false,       // first page loading
    val isLoadingMore: Boolean = false,   // subsequent pages loading
    val endReached: Boolean = false,
    val isFromCache: Boolean = false,
    val error: AppError? = null,
)
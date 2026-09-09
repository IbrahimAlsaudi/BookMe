package com.example.bookme.presentation.hoteldetail

import com.example.bookme.domain.error.AppError
import com.example.bookme.domain.model.hotel.Hotel

data class HotelDetailUiState(
    val hotel: Hotel? = null,
    val isLoading: Boolean = false,
    val error: AppError? = null
)

package com.example.bookme.presentation.booking

import com.example.bookme.domain.model.booking.BookingPriceBreakdown
import com.example.bookme.domain.model.hotel.Hotel
import java.time.LocalDate

data class BookingUiState(
    val hotel: Hotel? = null,
    val isLoading: Boolean = false,
    val checkInDate: LocalDate? = null,
    val checkOutDate: LocalDate? = null,
    val rooms: Int = 1,
    val priceBreakdown: BookingPriceBreakdown? = null,
    val bookingReference: String? = null,
    val errorResId: Int? = null,
)
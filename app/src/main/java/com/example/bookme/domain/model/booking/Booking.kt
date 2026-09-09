package com.example.bookme.domain.model.booking

import java.time.LocalDate

const val VAT_RATE = 0.15

data class BookingRequest(
    val hotelId: String,
    val checkIn: LocalDate,
    val checkOut: LocalDate,
    val rooms: Int,
    val pricePerNight: Double,
)

sealed interface DateValidationResult {
    data object Valid : DateValidationResult
    data class Invalid(val resourceId: Int) : DateValidationResult
}

data class BookingPriceBreakdown(
    val nights: Int,
    val rooms: Int,
    val basePrice: Double,
    val vatAmount: Double,
    val total: Double,
)

data class ConfirmedBooking(
    val bookingReference: String,
    val hotelId: String,
    val hotelName: String,
    val checkIn: LocalDate,
    val checkOut: LocalDate,
    val rooms: Int,
    val priceBreakdown: BookingPriceBreakdown,
)

sealed interface BookingResult {
    data class Success(val booking: ConfirmedBooking) : BookingResult
    data class ValidationFailed(val resourceId: Int) : BookingResult
}
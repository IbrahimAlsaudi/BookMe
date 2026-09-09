package com.example.bookme.domain.usecase

import com.example.bookme.domain.model.booking.BookingPriceBreakdown
import com.example.bookme.domain.model.booking.BookingRequest
import com.example.bookme.domain.model.booking.VAT_RATE
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class CalculateBookingPriceUseCase @Inject constructor() {

    operator fun invoke(request: BookingRequest): BookingPriceBreakdown {
        val nights = ChronoUnit.DAYS.between(request.checkIn, request.checkOut).toInt().coerceAtLeast(0)
        val basePrice = nights * request.rooms * request.pricePerNight
        val vatAmount = basePrice * VAT_RATE
        val total = basePrice + vatAmount
        return BookingPriceBreakdown(
            nights = nights,
            rooms = request.rooms,
            basePrice = basePrice,
            vatAmount = vatAmount,
            total = total,
        )
    }
}
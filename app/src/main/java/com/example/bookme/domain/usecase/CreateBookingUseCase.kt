package com.example.bookme.domain.usecase

import com.example.bookme.domain.model.booking.BookingRequest
import com.example.bookme.domain.model.booking.BookingResult
import com.example.bookme.domain.model.booking.ConfirmedBooking
import com.example.bookme.domain.model.booking.DateValidationResult
import java.time.LocalDate
import javax.inject.Inject
import kotlin.random.Random

class CreateBookingUseCase @Inject constructor(
    private val validateBookingDates: ValidateBookingDatesUseCase,
    private val calculateBookingPrice: CalculateBookingPriceUseCase,
) {
    operator fun invoke(
        hotelId: String,
        hotelName: String,
        pricePerNight: Double,
        checkIn: LocalDate?,
        checkOut: LocalDate?,
        rooms: Int,
    ): BookingResult {
        val validation = validateBookingDates(checkIn, checkOut, rooms)
        if (validation is DateValidationResult.Invalid) {
            return BookingResult.ValidationFailed(validation.resourceId)
        }
        checkNotNull(checkIn)
        checkNotNull(checkOut)

        val breakdown = calculateBookingPrice(
            BookingRequest(hotelId, checkIn, checkOut, rooms, pricePerNight)
        )

        return BookingResult.Success(
            ConfirmedBooking(
                bookingReference = generateReference(),
                hotelId = hotelId,
                hotelName = hotelName,
                checkIn = checkIn,
                checkOut = checkOut,
                rooms = rooms,
                priceBreakdown = breakdown,
            )
        )
    }

    private fun generateReference(): String = "BM-${Random.nextInt(100000, 999999)}"
}
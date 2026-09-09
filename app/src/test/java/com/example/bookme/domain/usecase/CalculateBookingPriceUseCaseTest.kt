package com.example.bookme.domain.usecase

import com.example.bookme.domain.model.booking.BookingRequest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class CalculateBookingPriceUseCaseTest {

    private val useCase = CalculateBookingPriceUseCase()

    @Test
    fun `calculates correct price for multiple nights and rooms`() {
        // 3200 EGP per night, 1 night, 3 rooms = 9600
        // VAT 15% = 1440
        // Total = 11040
        val request = BookingRequest(
            hotelId = "1",
            checkIn = LocalDate.of(2026, 9, 3),
            checkOut = LocalDate.of(2026, 9, 4),
            rooms = 3,
            pricePerNight = 3200.0
        )

        val breakdown = useCase(request)

        assertEquals(1, breakdown.nights)
        assertEquals(3, breakdown.rooms)
        assertEquals(9600.0, breakdown.basePrice, 0.01)
        assertEquals(1440.0, breakdown.vatAmount, 0.01)
        assertEquals(11040.0, breakdown.total, 0.01)
    }

    @Test
    fun `when check-in equals check-out, nights is 0 and total is 0`() {
        val request = BookingRequest(
            hotelId = "1",
            checkIn = LocalDate.of(2026, 9, 3),
            checkOut = LocalDate.of(2026, 9, 3),
            rooms = 1,
            pricePerNight = 100.0
        )

        val breakdown = useCase(request)

        assertEquals(0, breakdown.nights)
        assertEquals(0.0, breakdown.total, 0.01)
    }
}

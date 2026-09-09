package com.example.bookme.domain.usecase

import com.example.bookme.R
import com.example.bookme.domain.model.booking.DateValidationResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class ValidateBookingDatesUseCaseTest {

    private val useCase = ValidateBookingDatesUseCase()
    private val today = LocalDate.of(2026, 9, 10)

    @Test
    fun `when check-in or check-out is null, returns missing dates error`() {
        val result = useCase(null, null, 1, today)
        assertTrue(result is DateValidationResult.Invalid)
        assertEquals(R.string.validation_error_missing_dates, (result as DateValidationResult.Invalid).resourceId)
    }

    @Test
    fun `when check-in is in the past, returns past date error`() {
        val checkIn = today.minusDays(1)
        val checkOut = today.plusDays(1)
        val result = useCase(checkIn, checkOut, 1, today)
        assertTrue(result is DateValidationResult.Invalid)
        assertEquals(R.string.validation_error_past_date, (result as DateValidationResult.Invalid).resourceId)
    }

    @Test
    fun `when check-out is before check-in, returns invalid range error`() {
        val checkIn = today.plusDays(2)
        val checkOut = today.plusDays(1)
        val result = useCase(checkIn, checkOut, 1, today)
        assertTrue(result is DateValidationResult.Invalid)
        assertEquals(R.string.validation_error_invalid_range, (result as DateValidationResult.Invalid).resourceId)
    }

    @Test
    fun `when rooms is less than 1, returns no rooms error`() {
        val checkIn = today.plusDays(1)
        val checkOut = today.plusDays(2)
        val result = useCase(checkIn, checkOut, 0, today)
        assertTrue(result is DateValidationResult.Invalid)
        assertEquals(R.string.validation_error_no_rooms, (result as DateValidationResult.Invalid).resourceId)
    }

    @Test
    fun `when rooms is more than 10, returns too many rooms error`() {
        val checkIn = today.plusDays(1)
        val checkOut = today.plusDays(2)
        val result = useCase(checkIn, checkOut, 11, today)
        assertTrue(result is DateValidationResult.Invalid)
        assertEquals(R.string.validation_error_too_many_rooms, (result as DateValidationResult.Invalid).resourceId)
    }

    @Test
    fun `when dates and rooms are valid, returns Valid`() {
        val checkIn = today.plusDays(1)
        val checkOut = today.plusDays(3)
        val result = useCase(checkIn, checkOut, 2, today)
        assertTrue(result is DateValidationResult.Valid)
    }
}

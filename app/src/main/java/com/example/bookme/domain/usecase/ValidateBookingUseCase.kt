package com.example.bookme.domain.usecase

import com.example.bookme.R
import com.example.bookme.domain.model.booking.DateValidationResult
import java.time.LocalDate
import javax.inject.Inject

class ValidateBookingDatesUseCase @Inject constructor() {

    operator fun invoke(
        checkIn: LocalDate?,
        checkOut: LocalDate?,
        rooms: Int,
        today: LocalDate = LocalDate.now(),
    ): DateValidationResult {
        if (checkIn == null || checkOut == null) {
            return DateValidationResult.Invalid(R.string.validation_error_missing_dates)
        }
        if (checkIn.isBefore(today)) {
            return DateValidationResult.Invalid(R.string.validation_error_past_date)
        }
        if (!checkOut.isAfter(checkIn)) {
            return DateValidationResult.Invalid(R.string.validation_error_invalid_range)
        }
        if (rooms < 1) {
            return DateValidationResult.Invalid(R.string.validation_error_no_rooms)
        }
        if (rooms > 10) {
            return DateValidationResult.Invalid(R.string.validation_error_too_many_rooms)
        }
        return DateValidationResult.Valid
    }
}

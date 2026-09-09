package com.example.bookme.presentation.booking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookme.R
import com.example.bookme.domain.model.booking.BookingResult
import com.example.bookme.domain.usecase.CalculateBookingPriceUseCase
import com.example.bookme.domain.usecase.CreateBookingUseCase
import com.example.bookme.domain.usecase.GetHotelByIdUseCase
import com.example.bookme.domain.model.booking.BookingRequest
import com.example.bookme.domain.error.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getHotelById: GetHotelByIdUseCase,
    private val calculatePrice: CalculateBookingPriceUseCase,
    private val createBooking: CreateBookingUseCase,
) : ViewModel() {

    private val hotelId: String = checkNotNull(savedStateHandle["hotelId"])

    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()

    init {
        loadHotel()
    }

    private fun loadHotel() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = getHotelById(hotelId)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(hotel = result.data, isLoading = false) }
                    updatePrice()
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorResId = R.string.error_server) }
                }
            }
        }
    }

    fun onDateSelected(checkIn: LocalDate?, checkOut: LocalDate?) {
        _uiState.update { it.copy(checkInDate = checkIn, checkOutDate = checkOut, errorResId = null) }
        updatePrice()
    }

    fun onRoomsChanged(rooms: Int) {
        _uiState.update { it.copy(rooms = rooms.coerceIn(1, 10), errorResId = null) }
        updatePrice()
    }

    private fun updatePrice() {
        val state = _uiState.value
        val hotel = state.hotel ?: return
        if (state.checkInDate != null && state.checkOutDate != null) {
            val breakdown = calculatePrice(
                BookingRequest(
                    hotelId = hotel.id,
                    checkIn = state.checkInDate,
                    checkOut = state.checkOutDate,
                    rooms = state.rooms,
                    pricePerNight = hotel.pricePerNight
                )
            )
            _uiState.update { it.copy(priceBreakdown = breakdown) }
        } else {
            _uiState.update { it.copy(priceBreakdown = null) }
        }
    }

    fun onConfirmBooking() {
        val state = _uiState.value
        val hotel = state.hotel ?: return
        
        val result = createBooking(
            hotelId = hotel.id,
            hotelName = hotel.name,
            pricePerNight = hotel.pricePerNight,
            checkIn = state.checkInDate,
            checkOut = state.checkOutDate,
            rooms = state.rooms
        )

        when (result) {
            is BookingResult.Success -> {
                _uiState.update { it.copy(bookingReference = result.booking.bookingReference) }
            }
            is BookingResult.ValidationFailed -> {
                _uiState.update { it.copy(errorResId = result.resourceId) }
            }
        }
    }
}

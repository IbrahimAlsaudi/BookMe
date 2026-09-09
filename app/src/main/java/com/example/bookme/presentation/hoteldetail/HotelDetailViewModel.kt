package com.example.bookme.presentation.hoteldetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookme.domain.error.Resource
import com.example.bookme.domain.usecase.GetHotelByIdUseCase
import com.example.bookme.domain.usecase.ObserveIsFavoriteUseCase
import com.example.bookme.domain.usecase.ToggleFavoriteUseCase
import androidx.navigation.toRoute
import com.example.bookme.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HotelDetailViewModel @Inject constructor(
    private val getHotelById: GetHotelByIdUseCase,
    private val observeIsFavorite: ObserveIsFavoriteUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val hotelId: String = savedStateHandle.toRoute<Screen.HotelDetail>().hotelId

    private val _uiState = MutableStateFlow(HotelDetailUiState())
    
    val uiState: StateFlow<HotelDetailUiState> = combine(
        _uiState,
        observeIsFavorite(hotelId)
    ) { state, isFavorite ->
        state.copy(hotel = state.hotel?.copy(isFavorite = isFavorite))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HotelDetailUiState(isLoading = true))

    init {
        loadHotel()
    }

    fun loadHotel() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = getHotelById(hotelId)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(hotel = result.data, isLoading = false) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(error = result.error, isLoading = false) }
                }
            }
        }
    }

    fun onToggleFavorite() {
        val currentHotel = _uiState.value.hotel ?: return
        viewModelScope.launch {
            toggleFavorite(currentHotel.id, !currentHotel.isFavorite)
        }
    }
}

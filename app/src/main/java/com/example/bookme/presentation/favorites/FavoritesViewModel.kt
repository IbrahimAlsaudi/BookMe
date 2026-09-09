package com.example.bookme.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookme.domain.model.hotel.Hotel
import com.example.bookme.domain.usecase.ObserveFavoritesUseCase
import com.example.bookme.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    observeFavorites: ObserveFavoritesUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase
) : ViewModel() {

    val favorites: StateFlow<List<Hotel>> = observeFavorites()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onToggleFavorite(hotelId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            toggleFavorite(hotelId, isFavorite)
        }
    }
}

package com.example.bookme.domain.usecase

import com.example.bookme.domain.model.hotel.Hotel
import com.example.bookme.domain.repository.HotelRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFavoritesUseCase @Inject constructor(
    private val repository: HotelRepository
) {
    operator fun invoke(): Flow<List<Hotel>> = repository.observeFavorites()
}
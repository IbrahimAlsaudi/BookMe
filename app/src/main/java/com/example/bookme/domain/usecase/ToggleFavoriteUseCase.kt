package com.example.bookme.domain.usecase

import com.example.bookme.domain.repository.HotelRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: HotelRepository,
) {
    suspend operator fun invoke(id: String, isFavorite: Boolean) {
        repository.setFavorite(id, isFavorite)
    }
}
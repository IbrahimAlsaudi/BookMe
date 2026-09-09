package com.example.bookme.domain.usecase

import com.example.bookme.domain.repository.HotelRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveIsFavoriteUseCase @Inject constructor(
    private val repository: HotelRepository
) {
    operator fun invoke(id: String): Flow<Boolean> = repository.observeIsFavorite(id)
}
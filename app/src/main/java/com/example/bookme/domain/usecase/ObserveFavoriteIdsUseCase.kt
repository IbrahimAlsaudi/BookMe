package com.example.bookme.domain.usecase

import com.example.bookme.domain.repository.HotelRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFavoriteIdsUseCase @Inject constructor(
    private val repository: HotelRepository,
) {
    operator fun invoke(): Flow<Set<String>> = repository.observeFavoriteIds()
}
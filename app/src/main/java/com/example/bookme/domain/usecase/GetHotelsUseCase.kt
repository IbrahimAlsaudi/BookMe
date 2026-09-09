package com.example.bookme.domain.usecase

import com.example.bookme.domain.error.Resource
import com.example.bookme.domain.model.hotel.HotelQuery
import com.example.bookme.domain.repository.HotelPage
import com.example.bookme.domain.repository.HotelRepository
import javax.inject.Inject

class GetHotelsUseCase @Inject constructor(
    private val repository: HotelRepository
) {
    suspend operator fun invoke(query: HotelQuery): Resource<HotelPage> = repository.getHotels(query)
}
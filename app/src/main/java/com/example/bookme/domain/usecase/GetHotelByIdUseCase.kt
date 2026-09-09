package com.example.bookme.domain.usecase

import com.example.bookme.domain.error.Resource
import com.example.bookme.domain.model.hotel.Hotel
import com.example.bookme.domain.repository.HotelRepository
import javax.inject.Inject

class GetHotelByIdUseCase @Inject constructor(
    private val repository: HotelRepository
) {
    suspend operator fun invoke(id: String): Resource<Hotel> = repository.getHotelById(id)
}
package com.example.bookme.domain.repository

import com.example.bookme.domain.model.Hotel
import kotlinx.coroutines.flow.Flow

interface HotelRepository {
    fun getHotels(): Flow<List<Hotel>>
    suspend fun getHotelById(id: Long): Hotel?
    suspend fun toggleFavorite(id: Long)
    suspend fun searchHotels(query: String): List<Hotel>
}
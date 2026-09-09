package com.example.bookme.domain.repository

import com.example.bookme.domain.error.Resource
import com.example.bookme.domain.model.hotel.Hotel
import com.example.bookme.domain.model.hotel.HotelFilters
import com.example.bookme.domain.model.hotel.HotelQuery
import kotlinx.coroutines.flow.Flow

data class HotelPage(
    val hotels: List<Hotel>,
    val endReached: Boolean,
    val isFromCache: Boolean,
)

interface HotelRepository {
    suspend fun getHotels(query: HotelQuery): Resource<HotelPage>
    suspend fun getHotelById(id: String): Resource<Hotel>
    fun observeIsFavorite(id: String): Flow<Boolean>
    fun observeFavoriteIds(): Flow<Set<String>>
    fun observeFavorites(): Flow<List<Hotel>>
    suspend fun setFavorite(id: String, isFavorite: Boolean)
}
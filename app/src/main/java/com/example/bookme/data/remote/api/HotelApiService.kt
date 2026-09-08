package com.example.bookme.data.remote.api

import com.example.bookme.data.remote.dto.HotelDto
import retrofit2.http.GET
import retrofit2.http.Query

interface HotelApiService {
    @GET
    suspend fun getHotels(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("name") name: String? = null,
        @Query("city") city: String? = null,
        @Query("rating") rating: Double? = null
    ): List<HotelDto>
}
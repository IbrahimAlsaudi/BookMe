package com.example.bookme.data.remote.api

import com.example.bookme.data.remote.dto.HotelDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface HotelApiService {

    @GET("hotels")
    suspend fun getHotels(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("city") city: String? = null,
    ): List<HotelDto>

    @GET("hotels/{id}")
    suspend fun getHotelById(@Path("id") id: String): HotelDto
}
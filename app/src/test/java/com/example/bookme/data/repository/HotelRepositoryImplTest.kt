package com.example.bookme.data.repository

import com.example.bookme.data.local.dao.HotelDao
import com.example.bookme.data.local.entity.HotelEntity
import com.example.bookme.data.remote.api.HotelApiService
import com.example.bookme.data.remote.dto.HotelDto
import com.example.bookme.domain.error.Resource
import com.example.bookme.domain.model.hotel.HotelQuery
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class HotelRepositoryImplTest {

    private val api: HotelApiService = mock()
    private val dao: HotelDao = mock()
    private val repository = HotelRepositoryImpl(api, dao)

    private fun createMockDto(id: String, name: String = "Hotel") = HotelDto(
        id = id, 
        name = name, 
        city = "Cairo",
        country = "Egypt",
        rating = 4.5,
        pricePerNight = 100.0,
        currency = "USD",
        imageUrl = "",
        images = emptyList(),
        amenities = emptyList(),
        description = "",
        address = "",
        latitude = 0.0,
        longitude = 0.0
    )

    private fun createMockEntity(id: String) = HotelEntity(
        id = id, name = "Hotel $id", city = "Cairo", country = "Egypt",
        rating = 4.5, pricePerNight = 100.0, currency = "USD",
        imageUrl = "", images = emptyList(), amenities = emptyList(),
        description = "", address = "", latitude = 0.0, longitude = 0.0,
        isFavorite = false, cachedAt = 0L
    )

    @Test
    fun `getHotels returns success and caches data on network success`() = runTest {
        val dtos = listOf(createMockDto("1"), createMockDto("2"))
        // Match the 3 arguments: page, limit, city (nullable)
        whenever(api.getHotels(any(), any(), anyOrNull())).thenReturn(dtos)
        whenever(dao.getIsFavorite(any())).thenReturn(false)

        val query = HotelQuery(page = 0, pageSize = 20)
        val result = repository.getHotels(query)

        assertTrue("Expected Success, but got $result", result is Resource.Success)
        val page = (result as Resource.Success).data
        assertEquals(2, page.hotels.size)
        assertEquals(false, page.isFromCache)
        verify(api).getHotels(any(), any(), anyOrNull())
        verify(dao).upsertAll(any())
    }

    @Test
    fun `getHotels falls back to cache on network failure`() = runTest {
        whenever(api.getHotels(any(), any(), anyOrNull())).thenThrow(RuntimeException("Network error"))
        val cachedEntities = listOf(createMockEntity("1"), createMockEntity("2"))
        whenever(dao.getAllCached()).thenReturn(cachedEntities)

        val query = HotelQuery(page = 0, pageSize = 20)
        val result = repository.getHotels(query)

        assertTrue("Expected Success from cache, but got $result", result is Resource.Success)
        val page = (result as Resource.Success).data
        assertEquals(2, page.hotels.size)
        assertTrue(page.isFromCache)
    }

    @Test
    fun `getHotels applies search filtering correctly`() = runTest {
        val dtos = listOf(
            createMockDto(id = "1", name = "Hilton"),
            createMockDto(id = "2", name = "Marriott")
        )
        whenever(api.getHotels(any(), any(), anyOrNull())).thenReturn(dtos)
        whenever(dao.getIsFavorite(any())).thenReturn(false)

        val query = HotelQuery(searchText = "Hilton", page = 0, pageSize = 20)
        val result = repository.getHotels(query)

        assertTrue("Expected Success, but got $result", result is Resource.Success)
        val page = (result as Resource.Success).data
        assertEquals(1, page.hotels.size)
        assertEquals("Hilton", page.hotels[0].name)
    }
}

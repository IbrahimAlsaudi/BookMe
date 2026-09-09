package com.example.bookme.data.repository

import com.example.bookme.data.local.dao.HotelDao
import com.example.bookme.data.mapper.toDomain
import com.example.bookme.data.mapper.toEntity
import com.example.bookme.data.remote.api.HotelApiService
import com.example.bookme.domain.error.Resource
import com.example.bookme.domain.error.toAppError
import com.example.bookme.domain.model.hotel.Hotel
import com.example.bookme.domain.model.hotel.HotelQuery
import com.example.bookme.domain.repository.HotelPage
import com.example.bookme.domain.repository.HotelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HotelRepositoryImpl @Inject constructor(
    private val api: HotelApiService,
    private val dao: HotelDao,
) : HotelRepository {

    override suspend fun getHotels(query: HotelQuery): Resource<HotelPage> {
        return try {
            val dtos = api.getHotels(
                page = query.page + 1, // mockapi pages are 1-indexed our query is 0-indexed
                limit = query.pageSize,
                city = query.filter.city,
            )
            val now = System.currentTimeMillis()
            val entities = dtos.map { dto ->
                val existingFavorite = dao.getIsFavorite(dto.id) ?: false
                dto.toEntity(existingFavorite, now)
            }
            dao.upsertAll(entities)

            val hotels = entities.map { it.toDomain() }.applyFilters(query)

            val endReached = dtos.size < query.pageSize
            
            Resource.Success(
                HotelPage(hotels = hotels, endReached = endReached, isFromCache = false)
            )
        } catch (e: Exception) {
            val cached = dao.getAllCached().map { it.toDomain() }.applyFilters(query)
            val page = cached.drop(query.page * query.pageSize).take(query.pageSize)
            Resource.Success(
                HotelPage(hotels = page, endReached = page.size < query.pageSize, isFromCache = true)
            )
        }
    }

    override suspend fun getHotelById(id: String): Resource<Hotel> {
        return try {
            val dto = api.getHotelById(id)
            val existingFavorite = dao.getIsFavorite(id) ?: false
            val entity = dto.toEntity(existingFavorite, System.currentTimeMillis())
            dao.upsertAll(listOf(entity))
            Resource.Success(entity.toDomain())
        } catch (e: Exception) {
            val cached = dao.getById(id)
            if (cached != null) Resource.Success(cached.toDomain()) else Resource.Error(e.toAppError())
        }
    }

    override fun observeIsFavorite(id: String): Flow<Boolean> =
        dao.observeIsFavorite(id).map { it ?: false }

    override fun observeFavoriteIds(): Flow<Set<String>> =
        dao.observeFavoriteIds().map { it.toSet() }

    override fun observeFavorites(): Flow<List<Hotel>> =
        dao.observeFavorites().map { list -> list.map { it.toDomain() } }

    override suspend fun setFavorite(id: String, isFavorite: Boolean) {
        dao.setFavorite(id, isFavorite)
    }

    private fun List<Hotel>.applyFilters(query: HotelQuery): List<Hotel> {
        val filter = query.filter
        val predicates = buildList<(Hotel) -> Boolean> {
            if (query.searchText.isNotBlank()) {
                add { hotel -> hotel.name.contains(query.searchText, ignoreCase = true) }
            }
            filter.city?.let { city -> add { hotel -> hotel.city == city } }
            filter.minRating?.let { min -> add { hotel -> hotel.rating >= min } }
            filter.minPrice?.let { min -> add { hotel -> hotel.pricePerNight >= min } }
            filter.maxPrice?.let { max -> add { hotel -> hotel.pricePerNight <= max } }
        }
        return filter { hotel -> predicates.all { it(hotel) } }
    }
}
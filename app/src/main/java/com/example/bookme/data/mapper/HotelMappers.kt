package com.example.bookme.data.mapper

import com.example.bookme.data.local.entity.HotelEntity
import com.example.bookme.data.remote.dto.HotelDto
import com.example.bookme.domain.model.hotel.Hotel

fun HotelDto.toDomain(isFavorite: Boolean = false): Hotel {
    return Hotel(
        id = id,
        name = name,
        city = city,
        country = country,
        rating = rating,
        pricePerNight = pricePerNight,
        currency = currency,
        imageUrl = imageUrl,
        images = images,
        amenities = amenities,
        description = description,
        address = address,
        latitude = latitude,
        longitude = longitude,
        isFavorite = isFavorite
    )
}

fun HotelDto.toEntity(isFavorite: Boolean, cachedAt: Long): HotelEntity =
    HotelEntity(
        id = id,
        name = name,
        city = city,
        country = country,
        rating = rating,
        pricePerNight = pricePerNight,
        currency = currency,
        imageUrl = imageUrl,
        images = images,
        amenities = amenities,
        description = description,
        address = address,
        latitude = latitude,
        longitude = longitude,
        isFavorite = isFavorite,
        cachedAt = cachedAt,
    )

fun HotelEntity.toDomain(): Hotel =
    Hotel(
        id = id,
        name = name,
        city = city,
        country = country,
        rating = rating,
        pricePerNight = pricePerNight,
        currency = currency,
        imageUrl = imageUrl,
        images = images,
        amenities = amenities,
        description = description,
        address = address,
        latitude = latitude,
        longitude = longitude,
        isFavorite = isFavorite,
    )
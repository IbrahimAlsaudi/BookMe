package com.example.bookme.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bookme.data.local.entity.HotelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HotelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(hotels: List<HotelEntity>)

    @Query("SELECT * FROM hotels WHERE id = :id")
    suspend fun getById(id: String): HotelEntity?

    @Query("SELECT * FROM hotels")
    suspend fun getAllCached(): List<HotelEntity>

    @Query("SELECT isFavorite FROM hotels WHERE id = :id")
    suspend fun getIsFavorite(id: String): Boolean?

    @Query("UPDATE hotels SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: String, isFavorite: Boolean)

    @Query("SELECT isFavorite FROM hotels WHERE id = :id")
    fun observeIsFavorite(id: String): Flow<Boolean?>

    @Query("SELECT id FROM hotels WHERE isFavorite = 1")
    fun observeFavoriteIds(): Flow<List<String>>

    @Query("SELECT * FROM hotels WHERE isFavorite = 1 ORDER BY name ASC")
    fun observeFavorites(): Flow<List<HotelEntity>>
}
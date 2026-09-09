package com.example.bookme.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.bookme.data.local.dao.HotelDao
import com.example.bookme.data.local.entity.HotelEntity

@Database(entities = [HotelEntity::class], version = 1, exportSchema = false)
abstract class BookMeDatabase : RoomDatabase() {
    abstract fun hotelDao(): HotelDao
}
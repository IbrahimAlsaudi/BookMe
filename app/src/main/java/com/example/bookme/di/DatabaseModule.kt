package com.example.bookme.di

import android.content.Context
import androidx.room.Room
import com.example.bookme.data.local.dao.HotelDao
import com.example.bookme.data.local.database.BookMeDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): BookMeDatabase =
        Room.databaseBuilder(context, BookMeDatabase::class.java, "bookme.db").build()

    @Provides
    @Singleton
    fun provideHotelDao(database: BookMeDatabase): HotelDao = database.hotelDao()
}
package com.example.bookme.di

import com.example.bookme.data.repository.HotelRepositoryImpl
import com.example.bookme.domain.repository.HotelRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHotelRepository(impl: HotelRepositoryImpl): HotelRepository
}
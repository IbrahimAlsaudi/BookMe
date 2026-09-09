package com.example.bookme.presentation.favorites

import app.cash.turbine.test
import com.example.bookme.domain.model.hotel.Hotel
import com.example.bookme.domain.usecase.ObserveFavoritesUseCase
import com.example.bookme.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

    private val observeFavorites: ObserveFavoritesUseCase = mock()
    private val toggleFavorite: ToggleFavoriteUseCase = mock()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createMockHotel(id: String, name: String) = Hotel(
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
        longitude = 0.0,
        isFavorite = true
    )

    @Test
    fun `initial state reflects observed favorites`() = runTest {
        val mockFavorites = listOf(
            createMockHotel("1", "Hotel 1"),
            createMockHotel("2", "Hotel 2")
        )
        whenever(observeFavorites()).thenReturn(flowOf(mockFavorites))

        val viewModel = FavoritesViewModel(observeFavorites, toggleFavorite)

        viewModel.favorites.test {
            // StateFlow always emits the current value immediately upon subscription.
            // When FavoritesViewModel is initialized, favorites StateFlow is created.
            // Since use case emission happens after the flow is set up, the first item is the initial emptyList()
            assertEquals(emptyList<Hotel>(), awaitItem()) 
            assertEquals(mockFavorites, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onToggleFavorite calls toggleFavorite use case`() = runTest {
        whenever(observeFavorites()).thenReturn(flowOf(emptyList()))
        val viewModel = FavoritesViewModel(observeFavorites, toggleFavorite)
        
        viewModel.onToggleFavorite("1", false)
        advanceUntilIdle()

        verify(toggleFavorite).invoke("1", false)
    }
}

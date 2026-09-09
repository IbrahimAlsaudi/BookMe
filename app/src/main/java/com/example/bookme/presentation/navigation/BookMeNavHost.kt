package com.example.bookme.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.bookme.presentation.booking.BookingScreen
import com.example.bookme.presentation.favorites.FavoritesScreen
import com.example.bookme.presentation.hoteldetail.HotelDetailScreen
import com.example.bookme.presentation.hotellist.HotelListScreen

@Composable
fun BookMeNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.HotelList,
        modifier = modifier
    ) {
        composable<Screen.HotelList> {
            HotelListScreen(
                onHotelClick = { hotelId ->
                    navController.navigate(Screen.HotelDetail(hotelId))
                }
            )
        }
        composable<Screen.Favorites> {
            FavoritesScreen(
                onHotelClick = { hotelId ->
                    navController.navigate(Screen.HotelDetail(hotelId))
                }
            )
        }
        composable<Screen.HotelDetail> { backStackEntry ->
            val hotelDetail: Screen.HotelDetail = backStackEntry.toRoute()
            HotelDetailScreen(
                onBackClick = { navController.popBackStack() },
                onBookClick = {
                    navController.navigate(Screen.Booking(hotelDetail.hotelId))
                }
            )
        }
        composable<Screen.Booking> {
            BookingScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

package com.example.bookme.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.bookme.R
import com.example.bookme.presentation.navigation.BookMeNavHost
import com.example.bookme.presentation.navigation.Screen

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.hierarchy?.any {
        it.hasRoute<Screen.HotelList>() || it.hasRoute<Screen.Favorites>()
    } == true

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.hasRoute<Screen.HotelList>() } == true,
                        onClick = {
                            navController.navigate(Screen.HotelList) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Filled.Home, contentDescription = stringResource(R.string.nav_hotels)) },
                        label = { Text(stringResource(R.string.nav_hotels)) }
                    )
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.hasRoute<Screen.Favorites>() } == true,
                        onClick = {
                            navController.navigate(Screen.Favorites) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Filled.Favorite, contentDescription = stringResource(R.string.nav_favorites)) },
                        label = { Text(stringResource(R.string.nav_favorites)) }
                    )
                }
            }
        }
    ) { innerPadding ->
        BookMeNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

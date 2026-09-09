package com.example.bookme.presentation.hoteldetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bookme.R
import com.example.bookme.domain.error.AppError
import com.example.bookme.domain.model.hotel.Hotel
import com.example.bookme.presentation.components.ErrorState
import com.example.bookme.presentation.components.HotelAmenities
import com.example.bookme.presentation.components.HotelImageGallery

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelDetailScreen(
    onBackClick: () -> Unit,
    onBookClick: () -> Unit,
    viewModel: HotelDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.details_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_content_description))
                    }
                },
                actions = {
                    uiState.hotel?.let { hotel ->
                        IconButton(onClick = viewModel::onToggleFavorite) {
                            Icon(
                                imageVector = if (hotel.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = if (hotel.isFavorite) stringResource(R.string.remove_from_favorites) else stringResource(R.string.add_to_favorites),
                                tint = if (hotel.isFavorite) Color.Red else LocalContentColor.current
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.error != null -> {
                    ErrorState(
                        message = uiState.error?.toDisplayMessage() ?: stringResource(R.string.unknown_error),
                        onRetry = viewModel::loadHotel,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.hotel != null -> {
                    HotelDetailContent(
                        hotel = uiState.hotel!!,
                        onBookClick = onBookClick
                    )
                }
            }
        }
    }
}

@Composable
private fun HotelDetailContent(
    hotel: Hotel,
    onBookClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        HotelImageGallery(hotel = hotel)

        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = hotel.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFB300))
                    Text(
                        text = hotel.rating.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Icon(
                    Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "${hotel.address}, ${hotel.city}, ${hotel.country}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text(
                text = stringResource(R.string.description_label),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = hotel.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.amenities_label),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            HotelAmenities(amenities = hotel.amenities)

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = stringResource(R.string.price_label), style = MaterialTheme.typography.labelMedium)
                        Text(
                            text = "${hotel.currency} ${hotel.pricePerNight}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(text = stringResource(R.string.per_night_label), style = MaterialTheme.typography.labelSmall)
                    }
                    Button(onClick = onBookClick) {
                        Text(stringResource(R.string.book_now_button))
                    }
                }
            }
        }
    }
}

@Composable
private fun AppError.toDisplayMessage(): String = when (this) {
    AppError.NoInternet -> stringResource(R.string.error_no_internet)
    AppError.NotFound -> stringResource(R.string.error_not_found)
    AppError.ServerError -> stringResource(R.string.error_server)
    AppError.Timeout -> stringResource(R.string.error_timeout)
    is AppError.Unknown -> message ?: stringResource(R.string.unknown_error)
}

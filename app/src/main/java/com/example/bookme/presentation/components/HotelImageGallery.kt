package com.example.bookme.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.bookme.domain.model.hotel.Hotel

@Composable
fun HotelImageGallery(hotel: Hotel) {
    val allImages = remember(hotel) { listOf(hotel.imageUrl) + hotel.images }
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(allImages) { imageUrl ->
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillParentMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            )
        }
    }
}

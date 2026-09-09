package com.example.bookme.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.bookme.R
import com.example.bookme.domain.model.hotel.HotelFilters

private val CITIES = listOf("Cairo", "Alexandria", "Giza", "Luxor", "Aswan", "Hurghada", "Sharm El Sheikh", "Dahab")
private val RATING_TIERS = listOf(null, 3.0, 4.0, 4.5) // null = "Any"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    currentFilter: HotelFilters,
    onApply: (HotelFilters) -> Unit,
    onDismiss: () -> Unit,
) {
    var city by remember { mutableStateOf(currentFilter.city) }
    var minRating by remember { mutableStateOf(currentFilter.minRating) }
    var minPriceText by remember { mutableStateOf(currentFilter.minPrice?.toInt()?.toString() ?: "") }
    var maxPriceText by remember { mutableStateOf(currentFilter.maxPrice?.toInt()?.toString() ?: "") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(stringResource(R.string.filters_title), style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))

            Text(stringResource(R.string.filter_city), style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(selected = city == null, onClick = { city = null }, label = { Text(stringResource(R.string.filter_any)) })
                }
                items(CITIES) { c ->
                    FilterChip(selected = city == c, onClick = { city = c }, label = { Text(c) })
                }
            }

            Spacer(Modifier.height(20.dp))
            Text(stringResource(R.string.filter_rating), style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                RATING_TIERS.forEach { tier ->
                    FilterChip(
                        selected = minRating == tier,
                        onClick = { minRating = tier },
                        label = { Text(if (tier == null) stringResource(R.string.filter_any) else "${tier}+") },
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            Text(stringResource(R.string.filter_price), style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = minPriceText,
                    onValueChange = { minPriceText = it.filter(Char::isDigit) },
                    label = { Text(stringResource(R.string.filter_min_price)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = maxPriceText,
                    onValueChange = { maxPriceText = it.filter(Char::isDigit) },
                    label = { Text(stringResource(R.string.filter_max_price)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                )
            }

            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = {
                        city = null; minRating = null; minPriceText = ""; maxPriceText = ""
                        onApply(HotelFilters())
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                ) { Text(stringResource(R.string.filter_clear)) }

                Button(
                    onClick = {
                        onApply(
                            HotelFilters(
                                city = city,
                                minRating = minRating,
                                minPrice = minPriceText.toDoubleOrNull(),
                                maxPrice = maxPriceText.toDoubleOrNull(),
                            )
                        )
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                ) { Text(stringResource(R.string.filter_apply)) }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

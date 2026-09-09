package com.example.bookme.presentation.booking

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bookme.R
import com.example.bookme.presentation.components.*
import java.time.Instant
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    onBackClick: () -> Unit,
    viewModel: BookingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    var showDatePicker by remember { mutableStateOf(value = false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.booking_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_content_description))
                    }
                }
            )
        },
        bottomBar = {
            if (uiState.bookingReference == null) {
                Surface(
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp
                ) {
                    Button(
                        onClick = viewModel::onConfirmBooking,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = (!uiState.isLoading && uiState.checkInDate != null && uiState.checkOutDate != null)
                    ) {
                        Text(stringResource(R.string.confirm_reservation_button), style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.bookingReference != null) {
                BookingSuccessState(
                    reference = uiState.bookingReference!!,
                    onDone = onBackClick,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Hotel Info Summary
                    uiState.hotel?.let { hotel ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = hotel.name,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${hotel.city}, ${hotel.country}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "${hotel.currency}${hotel.pricePerNight}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    // Date Selection Section
                    BookingSection(title = stringResource(R.string.dates_section_title), icon = Icons.Default.DateRange) {
                        DateSelector(
                            checkIn = uiState.checkInDate,
                            checkOut = uiState.checkOutDate
                        ) { showDatePicker = true }
                    }

                    // Room Selection Section
                    BookingSection(title = stringResource(R.string.accommodation_section_title), icon = Icons.Default.Bed) {
                        RoomSelector(
                            count = uiState.rooms,
                            onCountChange = viewModel::onRoomsChanged
                        )
                    }

                    // Price Breakdown
                    uiState.priceBreakdown?.let { breakdown ->
                        BookingSection(title = stringResource(R.string.payment_section_title), icon = Icons.Default.Payments) {
                            PriceBreakdownCard(breakdown, uiState.hotel?.currency ?: "$")
                        }
                    }

                    // Error message
                    AnimatedVisibility(visible = uiState.errorResId != null) {
                        Text(
                            text = uiState.errorResId?.let { stringResource(it) } ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDateRangePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val start = datePickerState.selectedStartDateMillis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    }
                    val end = datePickerState.selectedEndDateMillis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    }
                    viewModel.onDateSelected(start, end)
                    showDatePicker = false
                }) {
                    Text(stringResource(R.string.date_dialog_select))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.date_dialog_cancel))
                }
            }
        ) {
            DateRangePicker(
                state = datePickerState,
                modifier = Modifier.weight(1f),
                title = { Text(stringResource(R.string.stay_period_title), modifier = Modifier.padding(16.dp)) },
                showModeToggle = false
            )
        }
    }
}

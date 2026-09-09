package com.example.bookme.presentation.hotellist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bookme.R
import com.example.bookme.domain.error.AppError
import com.example.bookme.presentation.components.ErrorState
import com.example.bookme.presentation.components.FilterBottomSheet
import com.example.bookme.presentation.components.HotelCard

@Composable
fun HotelListScreen(
    onHotelClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HotelListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchText by viewModel.searchText.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()
    var showFilterSheet by remember { mutableStateOf(false) }

    val shouldLoadMore by remember {
        derivedStateOf {
            val totalItems = uiState.hotels.size
            val lastVisibleItemIndex = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItemIndex >= totalItems - 5 &&
                    !uiState.endReached &&
                    !uiState.isLoading &&
                    !uiState.isLoadingMore
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) viewModel.loadNextPage()
    }

    Box(modifier = modifier.fillMaxSize()) {
        when {
            uiState.isLoading && uiState.hotels.isEmpty() -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            uiState.error != null && uiState.hotels.isEmpty() -> {
                ErrorState(
                    message = uiState.error.toDisplayMessage(),
                    onRetry = viewModel::retry,
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            else -> {
                LazyColumn(
                    state = lazyListState,
                    contentPadding = PaddingValues(bottom = 16.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    item {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = stringResource(R.string.welcome_message),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                                .fillMaxWidth(),
                        ) {
                            OutlinedTextField(
                                value = searchText,
                                onValueChange = viewModel::onSearchTextChanged,
                                placeholder = { Text(stringResource(R.string.search_hotels_placeholder)) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                    focusedContainerColor = MaterialTheme.colorScheme.surface
                                )
                            )
                            Spacer(Modifier.width(8.dp))
                            BadgedBox(badge = { if (uiState.filter.isActive) Badge() }) {
                                FilledTonalIconButton(
                                    onClick = { showFilterSheet = true },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.FilterList,
                                        contentDescription = stringResource(R.string.filters_content_description)
                                    )
                                }
                            }
                        }
                    }

                    if (uiState.isFromCache) {
                        item {
                            Surface(
                                color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        Icons.Default.WifiOff,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = stringResource(R.string.offline_message),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }
                            }
                        }
                    }

                    if (uiState.hotels.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillParentMaxHeight(0.7f)
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.no_search_results),
                                    modifier = Modifier.padding(16.dp),
                                )
                            }
                        }
                    } else {
                        items(items = uiState.hotels, key = { it.id }) { hotel ->
                            HotelCard(
                                hotel = hotel,
                                onClick = { onHotelClick(hotel.id) },
                                onFavoriteClick = { isFavorite ->
                                    viewModel.onToggleFavorite(
                                        hotel.id,
                                        isFavorite
                                    )
                                },
                                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                            )
                        }

                        if (uiState.isLoadingMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CircularProgressIndicator(strokeWidth = 2.dp)
                                }
                            }
                        }

                        if (uiState.error != null) {
                            item {
                                TextButton(
                                    onClick = viewModel::retry,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(stringResource(R.string.load_more_retry))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        FilterBottomSheet(
            currentFilter = uiState.filter,
            onApply = viewModel::onFilterChanged,
            onDismiss = { showFilterSheet = false },
        )
    }
}

@Composable
private fun AppError?.toDisplayMessage(): String = when (this) {
    AppError.NoInternet -> stringResource(R.string.error_no_internet)
    AppError.NotFound -> stringResource(R.string.error_not_found)
    AppError.ServerError -> stringResource(R.string.error_server)
    AppError.Timeout -> stringResource(R.string.error_timeout)
    is AppError.Unknown -> message ?: stringResource(R.string.unknown_error)
    null -> stringResource(R.string.something_went_wrong)
}

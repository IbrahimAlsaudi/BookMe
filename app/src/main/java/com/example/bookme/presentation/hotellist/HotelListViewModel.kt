package com.example.bookme.presentation.hotellist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookme.domain.error.Resource
import com.example.bookme.domain.model.hotel.HotelFilters
import com.example.bookme.domain.model.hotel.HotelQuery
import com.example.bookme.domain.usecase.GetHotelsUseCase
import com.example.bookme.domain.usecase.ObserveFavoriteIdsUseCase
import com.example.bookme.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class HotelListViewModel @Inject constructor(
    private val getHotels: GetHotelsUseCase,
    observeFavoriteIds: ObserveFavoriteIdsUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HotelListUiState())
    private val filter = MutableStateFlow(HotelFilters())

    val searchText: StateFlow<String> = _uiState
        .map { it.searchText }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")


    private val favoriteIds: StateFlow<Set<String>> = observeFavoriteIds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    val uiState: StateFlow<HotelListUiState> = combine(_uiState, favoriteIds) { state, ids ->
        state.copy(hotels = state.hotels.map { it.copy(isFavorite = it.id in ids) })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HotelListUiState())

    private var currentPage = 0
    private var loadJob: Job? = null
    private var nextPageJob: Job? = null

    init {
        _uiState
            .map { it.searchText }
            .distinctUntilChanged()
            .debounce(300)
            .onEach { text ->
                val query = HotelQuery(searchText = text, filter = filter.value, page = 0)
                loadFirstPage(query)
            }
            .launchIn(viewModelScope)

        filter.onEach { f ->
            val query = HotelQuery(searchText = _uiState.value.searchText, filter = f, page = 0)
            loadFirstPage(query)
        }.launchIn(viewModelScope)
    }

    fun onSearchTextChanged(text: String) {
        _uiState.update { it.copy(searchText = text) }
    }

    fun onFilterChanged(newFilter: HotelFilters) {
        filter.value = newFilter
        _uiState.update { it.copy(filter = newFilter) }
    }

    fun loadNextPage() {
        val state = _uiState.value
        if (state.isLoading || state.isLoadingMore || state.endReached) return

        nextPageJob?.cancel()
        nextPageJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }
            val query = HotelQuery(
                searchText = searchText.value,
                filter = filter.value,
                page = currentPage + 1
            )
            when (val result = getHotels(query)) {
                is Resource.Success -> {
                    currentPage = query.page
                    _uiState.update {
                        it.copy(
                            hotels = it.hotels + result.data.hotels,
                            isLoadingMore = false,
                            endReached = result.data.endReached,
                            isFromCache = result.data.isFromCache,
                            error = null,
                        )
                    }
                }
                is Resource.Error -> _uiState.update { it.copy(isLoadingMore = false, error = result.error) }
            }
        }
    }

    fun retry() {
        val query = HotelQuery(searchText = searchText.value, filter = filter.value, page = 0)
        loadFirstPage(query)
    }

    fun onToggleFavorite(hotelId: String, isFavorite: Boolean) {
        viewModelScope.launch { toggleFavorite(hotelId, isFavorite) }
    }

    private fun loadFirstPage(query: HotelQuery) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = getHotels(query)) {
                is Resource.Success -> {
                    currentPage = 0
                    _uiState.update {
                        it.copy(
                            hotels = result.data.hotels,
                            searchText = query.searchText,
                            filter = query.filter,
                            isLoading = false,
                            endReached = result.data.endReached,
                            isFromCache = result.data.isFromCache,
                            error = null,
                        )
                    }
                }
                is Resource.Error -> _uiState.update { it.copy(isLoading = false, error = result.error) }
            }
        }
    }
}
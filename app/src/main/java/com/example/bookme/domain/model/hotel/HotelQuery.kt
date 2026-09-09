package com.example.bookme.domain.model.hotel

data class HotelQuery(
    val searchText: String = "",
    val filter: HotelFilters = HotelFilters(),
    val page: Int = 0,
    val pageSize: Int = 10,
)
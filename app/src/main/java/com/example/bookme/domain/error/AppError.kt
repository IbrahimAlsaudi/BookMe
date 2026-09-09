package com.example.bookme.domain.error

sealed class AppError {
    object NoInternet : AppError()
    object Timeout : AppError()
    object ServerError : AppError()
    object NotFound : AppError()
    data class Unknown(val message: String?) : AppError()
}
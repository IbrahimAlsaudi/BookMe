package com.example.bookme.domain.error

import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun Throwable.toAppError(): AppError {
    return when(this) {
        is UnknownHostException -> AppError.NoInternet
        is SocketTimeoutException -> AppError.Timeout
        is HttpException -> when (code()) {
            404 -> AppError.NotFound
            in 500..599 -> AppError.ServerError
            else -> AppError.Unknown(message())
        }
        is IOException -> AppError.NoInternet
        else -> AppError.Unknown(message)
    }
}
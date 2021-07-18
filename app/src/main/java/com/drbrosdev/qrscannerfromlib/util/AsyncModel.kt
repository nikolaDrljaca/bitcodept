package com.drbrosdev.qrscannerfromlib.util

sealed class AsyncModel<out T> {
    data class Success<T>(val data: T): AsyncModel<T>()
    data class Fail<T>(val t: Throwable): AsyncModel<T>()
    object Loading: AsyncModel<Nothing>()
}
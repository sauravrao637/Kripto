package com.camo.kripto.utils

import com.camo.kripto.error.ErrorInfo

data class Resource<out T>(val status: Status, val data: T?, val errorInfo: ErrorInfo?) {
    companion object {
        fun <T> success(data: T): Resource<T> =
            Resource(status = Status.SUCCESS, data = data, errorInfo = null)

        fun <T> error(data: T?, errorInfo: ErrorInfo): Resource<T> =
            Resource(status = Status.ERROR, data = data, errorInfo = errorInfo)

        fun <T> loading(data: T?): Resource<T> =
            Resource(status = Status.LOADING, data = data, errorInfo = null)
    }
}
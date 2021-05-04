package com.dreamfolks.baseapp.connectivity


/**
 * To store all error type API response  data
 */
data class ErrorResponseInfo(
    val errorCode: String,
    val msg: String
) {
    override fun toString(): String {
        return ""
    }
}
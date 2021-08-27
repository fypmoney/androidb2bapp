package com.fypmoney.connectivity


/**
 * To store all error type API response  data
 */
data class ErrorResponseInfo(
    var errorCode: String,
    val msg: String,
    val data: String?= null
) {
    override fun toString(): String {
        return ""
    }
}
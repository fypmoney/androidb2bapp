package com.fypmoney.connectivity

import androidx.annotation.Keep


/**
 * To store all error type API response  data
 */
@Keep
data class ErrorResponseInfo(
    var errorCode: String,
    val msg: String,
    val data: String?= null
) {
    override fun toString(): String {
        return ""
    }
}
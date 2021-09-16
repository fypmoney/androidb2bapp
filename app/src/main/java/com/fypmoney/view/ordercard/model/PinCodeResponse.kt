package com.fypmoney.view.ordercard.model
import androidx.annotation.Keep

import com.google.gson.annotations.SerializedName


@Keep
data class PinCodeResponse(
    val `data`: PinCodeData
)

@Keep
data class PinCodeData(
    val city: String,
    val createdDate: String,
    val dispatchCentre: String,
    val pincode: String,
    val sortCode: String,
    val state: String,
    val stateCode: String,
    val stateGstCode: String,
    val status: String
)
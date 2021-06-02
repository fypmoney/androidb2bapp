package com.fypmoney.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class GetWalletBalanceResponse(
    @SerializedName("data") val getWalletBalanceResponseDetails: GetWalletBalanceResponseDetails
) : Serializable

data class GetWalletBalanceResponseDetails(
    @SerializedName("requestData") val requestData: String,
    @SerializedName("cardType") val cardType: String,
    @SerializedName("cardStatus") val cardStatus: String,
    @SerializedName("action") val action: String,
    @SerializedName("atmLimit") val atmLimit: String,
    @SerializedName("posLimit") val posLimit: String,
    @SerializedName("ecomLimit") val ecomLimit: String,
    @SerializedName("accountBalance") val accountBalance: String,
    @SerializedName("cl") val cl: String
) : Serializable
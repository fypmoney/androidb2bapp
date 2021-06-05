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

data class SendMoneyRequest(
    @SerializedName("txnType") val txnType: String? = null,
    @SerializedName("amount") val amount: String? = null,
    @SerializedName("beneficiaryId") val beneficiaryId: String? = null,
    @SerializedName("remarks") val remarks: String? = null,
    @SerializedName("mobileNo") val mobileNo: String? = null,
    @SerializedName("iFSCCode") val iFSCCode: String? = null,
    @SerializedName("accountNo") val accountNo: String? = null
) : BaseRequest()

data class SendMoneyResponse(
    @SerializedName("data") val sendMoneyResponseDetails: SendMoneyResponseDetails
) : Serializable

data class SendMoneyResponseDetails(
    @SerializedName("currentBalance") val currentBalance: String,
    @SerializedName("accountTxnId") val accountTxnId: String,
    @SerializedName("bankExternalId") val bankExternalId: String,
    @SerializedName("receiverName") val receiverName: String,
    @SerializedName("reveiverMobile") val reveiverMobile: String,
    @SerializedName("txnTime") val txnTime: String
)

    : Serializable

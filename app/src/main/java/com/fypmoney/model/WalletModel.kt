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
    @SerializedName("remainingLoadLimit") val remainingLoadLimit: String,
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
) : Serializable

data class RequestMoneyRequest(
    @SerializedName("requesteeMobile") val requesteeMobile: String? = null,
    @SerializedName("amount") val amount: String? = null,
    @SerializedName("emoji") val emoji: String? = null,
    @SerializedName("remarks") val remarks: String? = null
) : BaseRequest()

data class RequestMoneyResponse(
    @SerializedName("data") val requestMoneyResponseDetails: RequestMoneyResponseDetails
) : Serializable

data class RequestMoneyResponseDetails(
    @SerializedName("requesteeMobile") val requesteeMobile: String? = null,
    @SerializedName("amount") val amount: String? = null,
    @SerializedName("emoji") val emoji: String? = null,
    @SerializedName("remarks") val remarks: String? = null
) : Serializable


data class TransactionHistoryResponse(
    @SerializedName("data") val transactionHistoryResponseDetails: List<TransactionHistoryResponseDetails>
) : Serializable

data class TransactionHistoryResponseDetails(
    @SerializedName("id") var id: String?=null,
    @SerializedName("requesterUaaId") var requesterUaaId: String?=null,
    @SerializedName("cardId") var cardId: String?=null,
    @SerializedName("bankTxnStatus") var bankTxnStatus: String?=null,
    @SerializedName("txnTime") var txnTime: String?=null,
    @SerializedName("destinationUserId") var destinationUserId: String?=null,
    @SerializedName("destinationUserName") var destinationUserName: String?=null,
    @SerializedName("txnAmount") var txnAmount: String?=null
) : Serializable


data class TransactionHistoryRequest(
    @SerializedName("startDate") val startDate: String? = null,
    @SerializedName("endDate") val endDate: String? = null,
    @SerializedName("destinationUserId") val destinationUserId: String? = null
) : BaseRequest()





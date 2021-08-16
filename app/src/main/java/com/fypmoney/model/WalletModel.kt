package com.fypmoney.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class GetWalletBalanceResponse(
    @SerializedName("data") val getWalletBalanceResponseDetails: GetWalletBalanceResponseDetails
) : Serializable
@Keep
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
    @SerializedName("remainingWalletBalanceLimit") val remainingWalletBalanceLimit: String,
    @SerializedName("cl") val cl: String
) : Serializable
@Keep
data class SendMoneyRequest(
    @SerializedName("txnType") val txnType: String? = null,
    @SerializedName("amount") val amount: String? = null,
    @SerializedName("beneficiaryId") val beneficiaryId: String? = null,
    @SerializedName("remarks") val remarks: String? = null,
    @SerializedName("mobileNo") val mobileNo: String? = null,
    @SerializedName("iFSCCode") val iFSCCode: String? = null,
    @SerializedName("accountNo") val accountNo: String? = null
) : BaseRequest()
@Keep
data class SendMoneyResponse(
    @SerializedName("msg") val msg: String? = null,
    @SerializedName("data") val sendMoneyResponseDetails: SendMoneyResponseDetails
) : Serializable
@Keep
data class SendMoneyResponseDetails(
    @SerializedName("currentBalance") val currentBalance: String,
    @SerializedName("accountTxnId") val accountTxnId: String,
    @SerializedName("bankExternalId") val bankExternalId: String,
    @SerializedName("receiverName") val receiverName: String,
    @SerializedName("reveiverMobile") val reveiverMobile: String,
    @SerializedName("txnTime") val txnTime: String
) : Serializable
@Keep
data class RequestMoneyRequest(
    @SerializedName("requesteeMobile") val requesteeMobile: String? = null,
    @SerializedName("amount") val amount: String? = null,
    @SerializedName("emoji") val emoji: String? = null,
    @SerializedName("remarks") val remarks: String? = null
) : BaseRequest()
@Keep
data class RequestMoneyResponse(
    @SerializedName("msg") val msg: String? = null,
    @SerializedName("data") val requestMoneyResponseDetails: RequestMoneyResponseDetails
) : Serializable
@Keep
data class RequestMoneyResponseDetails(
    @SerializedName("requesteeMobile") val requesteeMobile: String? = null,
    @SerializedName("amount") val amount: String? = null,
    @SerializedName("emoji") val emoji: String? = null,
    @SerializedName("remarks") val remarks: String? = null
) : Serializable

@Keep
data class TransactionHistoryResponse(
    @SerializedName("data") val transactionHistoryResponseDetails: List<TransactionHistoryResponseDetails>
) : Serializable
@Keep
data class TransactionHistoryResponseDetails(

    @field:SerializedName("pgTxnNo")
    val pgTxnNo: String? = null,
    @field:SerializedName("destinationUserIdentifier")
    val destinationUserIdentifier: String? = null,

    @field:SerializedName("initiator")
    val initiator: Any? = null,

    @field:SerializedName("description")
    val description: Any? = null,

    @field:SerializedName("txnType")
    val txnType: String? = null,

    @field:SerializedName("destinationAccountIdentifier")
    val destinationAccountIdentifier: String? = null,

    @field:SerializedName("destinationUserName")
    val destinationUserName: String? = null,

    @field:SerializedName("isSender")
    val isSender: String? = null,

    @field:SerializedName("candidateForReversal")
    val candidateForReversal: Any? = null,

    @field:SerializedName("bankTxnTime")
    val bankTxnTime: Any? = null,

    @field:SerializedName("iFSCCode")
    val iFSCCode: Any? = null,

    @field:SerializedName("txnTime")
    val txnTime: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("bankTxnReversalNo")
    val bankTxnReversalNo: Any? = null,

    @field:SerializedName("destinationUserId")
    val destinationUserId: Int? = null,

    @field:SerializedName("emojis")
    val emojis: String? = null,

    @field:SerializedName("comments")
    val comments: String? = null,

    @field:SerializedName("sourceUserName")
    val sourceUserName: String? = null,

    @field:SerializedName("txnMode")
    val txnMode: Any? = null,

    @field:SerializedName("bankResponseMessage")
    val bankResponseMessage: String? = null,

    @field:SerializedName("bankTxnId")
    val bankTxnId: String? = null,

    @field:SerializedName("sourceUserIdentifier")
    val sourceUserIdentifier: Any? = null,

    @field:SerializedName("requesterUaaId")
    val requesterUaaId: Int? = null,

    @field:SerializedName("accountTxnNo")
    val accountTxnNo: String? = null,

    @field:SerializedName("pgTxnState")
    val pgTxnState: Any? = null,

    @field:SerializedName("sourceAccountIdentifier")
    val sourceAccountIdentifier: String? = null,

    @field:SerializedName("bankTxnStatus")
    val bankTxnStatus: String? = null,

    @field:SerializedName("pgTxnReversalNo")
    val pgTxnReversalNo: Any? = null,

    @field:SerializedName("bankId")
    val bankId: String? = null,

    @field:SerializedName("cardId")
    val cardId: Any? = null,

    @field:SerializedName("pgTxnStatus")
    val pgTxnStatus: Any? = null,

    @field:SerializedName("sourceUserId")
    val sourceUserId: Int? = null,

    @field:SerializedName("bankResponseCode")
    val bankResponseCode: String? = null,

    @field:SerializedName("txnAmount")
    val txnAmount: String? = null,

    @field:SerializedName("bankTxnState")
    val bankTxnState: String? = null,

    @field:SerializedName("isReversed")
    val isReversed: Any? = null
) : Serializable

@Keep
data class TransactionHistoryRequest(
    @SerializedName("startDate") val startDate: String? = null,
    @SerializedName("endDate") val endDate: String? = null,
    @SerializedName("destinationUserIdentifier") val destinationUserId: String? = null,
    @SerializedName("isbankTxnStatusSuccess") val isbankTxnStatusSuccess: Int? = 2
) : BaseRequest()

@Keep
data class TransactionHistoryRequestwithPage(
    @SerializedName("startDate") val startDate: String? = null,
    @SerializedName("endDate") val endDate: String? = null,
    @SerializedName("destinationUserIdentifier") val destinationUserId: String? = null,
    @SerializedName("isbankTxnStatusSuccess") val isbankTxnStatusSuccess: Int? = 2,
    @SerializedName("page") val page: Int? = null
) : BaseRequest()




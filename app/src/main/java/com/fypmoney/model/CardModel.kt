package com.fypmoney.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class VirtualCardRequestResponse(
    @SerializedName("data") val virtualCardRequestResponseDetails: VirtualCardRequestResponseDetails
) : Serializable


data class VirtualCardRequestResponseDetails(
    @SerializedName("requestData") val requestData: String,
    @SerializedName("serviceUrl") val serviceUrl: String
) : Serializable

data class FetchVirtualCardRequest(
    @SerializedName("action_name") var action_name: String? = null,
    @SerializedName("wlap_code") var wlap_code: String? = null,
    @SerializedName("wlap_secret_key") var wlap_secret_key: String? = null,
    @SerializedName("checksum") var checksum: String? = null,
    @SerializedName("p1") var p1: String? = null,
    @SerializedName("p2") var p2: String? = null
) : BaseRequest()

data class FetchVirtualCardResponse(
    @SerializedName("data") val fetchVirtualCardResponseDetails: FetchVirtualCardResponseDetails
) : Serializable


data class FetchVirtualCardResponseDetails(
    @SerializedName("message") val message: String,
    @SerializedName("card_number") val card_number: String,
    @SerializedName("expiry_month") val expiry_month: String,
    @SerializedName("expiry_year") val expiry_year: String,
    @SerializedName("cvv") val cvv: String,
    @SerializedName("transaction_type") val transactionTypeDetails: TransactionTypeDetails
) : Serializable

data class TransactionTypeDetails(
    @SerializedName("ECOM") val ECOM: String,
    @SerializedName("POS") val POS: String,
    @SerializedName("ATM") val ATM: String,
    @SerializedName("CL") val CL: String
) : Serializable

data class BankTransactionHistoryRequest(
    @SerializedName("startDate") val startDate: String? = null,
    @SerializedName("endDate") val endDate: String? = null
) : BaseRequest()


data class BankTransactionHistoryResponse(
    @SerializedName("data") val transactions: Transactions
) : Serializable


data class Transactions(
    @SerializedName("transactions") val bankTransactionHistoryResponseDetails: List<BankTransactionHistoryResponseDetails>,
    @SerializedName("pagination") val bankTransactionHistoryPagination: BankTransactionHistoryPagination
) : Serializable


data class BankTransactionHistoryResponseDetails(
    @SerializedName("transactionType") val transactionType: String,
    @SerializedName("amount") val amount: String,
    @SerializedName("paymentMode") val paymentMode: String,
    @SerializedName("mrn") val mrn: String,
    @SerializedName("bankReferenceNumber") val bankReferenceNumber: String,
    @SerializedName("message") val message: String,
    @SerializedName("transactionDate") val transactionDate: String,
    @SerializedName("userName") val userName: String,
    @SerializedName("mobileNo") val mobileNo: String,
    @SerializedName("accReferenceNumber") val accReferenceNumber: String
) : Serializable

data class BankTransactionHistoryPagination(
    @SerializedName("total_record") val total_record: Int,
    @SerializedName("per_page") val per_page: Int,
    @SerializedName("current_page") val current_page: Int,
    @SerializedName("total_pages") val total_pages: Int
) : Serializable


data class UpDateCardSettingsRequest(
    @SerializedName("action") val action: String? = null,
    @SerializedName("isEnable") val isEnable: String? = null,
    @SerializedName("channelType") val channelType: String? = null,
    @SerializedName("kitNumber") val kitNumber: String? = null,
    @SerializedName("cardType") val cardType: Int? = null,
) : BaseRequest()
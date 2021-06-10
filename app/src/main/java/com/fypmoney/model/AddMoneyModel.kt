package com.fypmoney.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AddMoneyModel(var name:String?=null,var imageUrl:String?=null) {
}

data class UpiModel(var name:String?=null,var imageUrl:String?=null) {
}

data class SavedCardResponseDetails(
    @SerializedName("name_on_card") val name_on_card: String,
    @SerializedName("card_type") val card_type: String,
    @SerializedName("card_token") val card_token: String,
    @SerializedName("is_expired") val is_expired: String,
    @SerializedName("card_no") val card_no: String,
    @SerializedName("card_brand") val card_brand: String,
    @SerializedName("expiry_year") val expiry_year: String,
    @SerializedName("expiry_month") val expiry_month: String,
) : Serializable


data class AddMoneyStep1Request(
    @SerializedName("remarks") val remarks: String?,
    @SerializedName("amount") val amount: String?
) : Serializable

data class AddMoneyStep1Response(
    @SerializedName("data") val addMoneyStep1ResponseDetails: AddMoneyStep1ResponseDetails) : Serializable

data class AddMoneyStep1ResponseDetails(
    @SerializedName("pgTxnNo") val pgTxnNo: String,
    @SerializedName("accountTxnNo") val accountTxnNo: String,
    @SerializedName("pgRequestData") val pgRequestData: String,
    ) : Serializable



data class PgRequestData(
    @SerializedName("transactionId") var transactionId: String?=null,
    @SerializedName("amount") var amount: String?=null,
    @SerializedName("phone") var phone: String?=null,
    @SerializedName("pgUrl") var pgUrl: String?=null,
    @SerializedName("userFirstName") var userFirstName: String?=null,
    @SerializedName("productName") var productName: String?=null,
    @SerializedName("email") var email: String?=null,
    @SerializedName("merchantKey") var merchantKey: String?=null,
    @SerializedName("merchantId") var merchantId: String?=null,
    @SerializedName("paymentHash") var paymentHash: String?=null,
) : Serializable
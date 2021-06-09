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
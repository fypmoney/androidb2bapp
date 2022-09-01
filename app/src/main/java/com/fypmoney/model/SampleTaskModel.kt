package com.fypmoney.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SampleTaskModel {

    @Keep
    data class SampleTaskDetails(
        @SerializedName("id") var id: String? = null,
        @SerializedName("name") var name: String? = null,
        @SerializedName("code") var code: String? = null,
        @SerializedName("description") var description: String? = null,
        @SerializedName("amount") var amount: String? = null,
        @SerializedName("numberOfDays") var numberOfDays: Int? = null,
        @SerializedName("resourceId") var resourceId: String? = null,
        @SerializedName("backgroundColor") var backgroundColor: String? = null,
        @SerializedName("status") var status: String? = null,
        @SerializedName("emojis") var emojis: String? = null
    ) : Serializable

}
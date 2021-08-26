package com.fypmoney.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SampleTaskModel {


    data class TasksResponse(
        @SerializedName("data") val sampleTaskResponseDetails: List<SampleTaskDetails> = mutableListOf()
    ) : Serializable

    data class TaskResponseById(
        @SerializedName("data") val sampleTaskResponseDetails: SampleTaskDetails
    ) : Serializable


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
package com.fypmoney.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class SplitBillsResponse(
    @SerializedName("data")
    val listOfArrays: List<String> = arrayListOf()
)
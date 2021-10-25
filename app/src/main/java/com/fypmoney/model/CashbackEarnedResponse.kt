package com.fypmoney.model
import androidx.annotation.Keep
import com.fypmoney.util.Utility

import com.google.gson.annotations.SerializedName


@Keep
data class CashbackEarnedResponse(
    val `data`: CashbackData
)

@Keep
data class CashbackData(
    val amount: Int
){
    fun getAmountInRuppes(): String? {
        return "₹${Utility.convertToRs("$amount")}"
    }
}
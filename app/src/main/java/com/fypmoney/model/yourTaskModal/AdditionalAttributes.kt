package com.fypmoney.model.yourTaskModal

import androidx.annotation.Keep

@Keep
data class AdditionalAttributes(
    val amount: Int,
    val description: String,
    val endDate: String,
    val is_payable: String,
    val list_details: String,
    val numberOfDays: Int,
    val startDate: String,
    val title: String,
    val worth_price: String
)
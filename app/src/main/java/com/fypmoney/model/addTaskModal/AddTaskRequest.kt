package com.fypmoney.model.addTaskModal

import androidx.annotation.Keep

@Keep
data class AddTaskRequest(
    val amount: String,
    val description: String,
    val endDate: String,
    val isPayable: Int,
    val paymentCurrency: String,
    val requesteeUuaId: String,
    val startDate: String,
    val title: String
)
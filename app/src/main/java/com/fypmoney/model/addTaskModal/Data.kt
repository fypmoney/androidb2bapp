package com.fypmoney.model.addTaskModal

data class Data(
    val amount: Int,
    val currentState: String,
    val description: String,
    val endDate: String,
    val id: Int,
    val isPayable: String,
    val isPaymentCompleted: String,
    val isTaskCompleted: String,
    val msg: String,
    val paymentCurrency: String,
    val paymentMode: Any,
    val paymentReference: Any,
    val recurringType: Any,
    val requesteeMobile: String,
    val requesteeName: String,
    val requesteeUuaId: Int,
    val requesterMobile: String,
    val requesterName: String,
    val requesterUuaId: Int,
    val startDate: String,
    val status: String,
    val taskType: String,
    val title: String
)
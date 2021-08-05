package com.fypmoney.model

data class UpdateTaskGetResponse(
    val requesteeName: String? = null,
    var msg: String? = null,
    val amount: Int? = null,
    val requesteeMobile: String? = null,
    val endDate: String? = null,
    val paymentMode: Any? = null,
    val requesterUuaId: Int? = null,
    val paymentReference: Any? = null,
    val description: String? = null,
    val title: String? = null,
    val recurringType: Any? = null,
    val taskType: String? = null,
    val isTaskCompleted: String? = null,
    val requesteeUuaId: Int? = null,
    val requesterName: String? = null,
    val bankPaymentReference: Any? = null,
    val id: Int? = null,
    val currentState: String? = null,
    val paymentCurrency: String? = null,
    val requesterMobile: String? = null,
    val startDate: String? = null,
    val isPayable: String? = null,
    val isPaymentCompleted: String? = null,
    val status: String? = null
)


package com.fypmoney.model

import androidx.annotation.Keep

@Keep
data class HistoryModelResponse(
    val requesteeName: String? = null,
    val msg: Any? = null,
    val amount: Int? = null,
    val requesteeMobile: String? = null,
    val endDate: String? = null,
    val paymentMode: Any? = null,
    val requesterUuaId: Int? = null,
    val paymentReference: Any? = null,
    val description: String? = null,
    val historyTitle: String? = null,
    val title: String? = null,
    val isTaskCompleted: String? = null,
    val requesteeUuaId: Int? = null,
    val requesterName: String? = null,
    val bankPaymentReference: Any? = null,
    val historyIconResourceId: String? = null,
    val id: Int? = null,
    val currentState: String? = null,
    val paymentCurrency: String? = null,
    val requesterMobile: String? = null,
    val startDate: String? = null,
    val isPayable: String? = null,
    val isPaymentCompleted: String? = null,
    val status: String? = null
)


package com.fypmoney.model.addTaskModal

data class AddTaskRequest(
    val amount: Int,
    val description: String,
    val endDate: String,
    val isPayable: Int,
    val paymentCurrency: String,
    val requesteeUuaId: Int,
    val startDate: String,
    val title: String
)
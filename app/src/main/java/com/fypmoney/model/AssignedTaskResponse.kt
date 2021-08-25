package com.fypmoney.model

import androidx.annotation.Keep

@Keep
data class AssignedTaskResponse(
    val requesteeName: String? = null,
    val amount: Int? = null,
    val requesteeMobile: String? = null,
    val processStatus: Int? = null,
    val endDate: String? = null,
    val requesterUuaId: Int? = null,
    val description: String? = null,
    val title: String? = null,
    val remainingTime: String? = null,
    val requesteeUuaId: Int? = null,
    val requesterName: String? = null,
    val isNewTask: String? = null,
    val overAllDays: String? = null,
    val id: Int? = null,
    val currentState: String? = null,
    val requesterMobile: String? = null,
    val startDate: String? = null,
    val emojis: String? = null
)


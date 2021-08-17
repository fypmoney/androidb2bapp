package com.fypmoney.model

import androidx.annotation.Keep

@Keep
data class SendTaskResponse(
    val emojis: String? = null,
    val comments: String? = null,
    val state: String? = null,
    val taskId: String? = null
)


package com.fypmoney.model

import androidx.annotation.Keep

@Keep
data class GetTaskResponse(
    val isAssignTask: Int? = null,
    val page: Int? = null,
    val size: Int? = null,
    val sort: String? = null

)
@Keep
data class GetTaskResponseIsassign(
    var isAssignTask: Int? = null,

    )


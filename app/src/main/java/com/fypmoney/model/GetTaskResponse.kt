package com.fypmoney.model

data class GetTaskResponse(
    val isAssignTask: Int? = null,
    val page: Int? = null,
    val size: Int? = null,
    val sort: String? = null

)

data class GetTaskResponseIsassign(
    val isAssignTask: Int? = null,

    )


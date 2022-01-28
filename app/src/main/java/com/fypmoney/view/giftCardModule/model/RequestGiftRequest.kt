package com.fypmoney.view.giftCardModule.model

import androidx.annotation.Keep

@Keep
data class RequestGiftRequest(
    val brands: List<String?>? = emptyList(),
    var searchCriteria: String = "",
    val categories: List<String?>? = emptyList()
)

@Keep
data class RequestGiftswithPage(
    val request: RequestGiftRequest? = null,
    val page: Int? = null,
    val size: Int? = null,
    val sort: String? = null

)


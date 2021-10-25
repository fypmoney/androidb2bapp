package com.fypmoney.model

import androidx.annotation.Keep

@Keep
data class QueryPaginationParams(

    val page: Int? = null,
    val size: Int = 10,
    val sort: String? = null

)



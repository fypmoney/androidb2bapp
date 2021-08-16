package com.fypmoney.model

import androidx.annotation.Keep

@Keep
data class StoreDataModel(
    val image: String? = null,
    var title: String? = null,
    var url: String? = null,
    var Icon: Int? = null
)


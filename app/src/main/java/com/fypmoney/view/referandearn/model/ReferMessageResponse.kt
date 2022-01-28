package com.fypmoney.view.referandearn.model

import androidx.annotation.Keep

@Keep
data class ReferMessageResponse (
    val maxReferredCount: Any? = null,
    val refereeAmount:Int = 0,
    val referLine2: String? = null,
    val rfu2: String? = null,
    val referMessage: String? = null,
    val rfu1: String? = null,
    val referrerAmount:Int = 0,
    val referLine1: String? = null,
    val rfu3: Any? = null
)
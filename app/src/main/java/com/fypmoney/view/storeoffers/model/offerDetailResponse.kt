package com.fypmoney.view.storeoffers.model

import androidx.annotation.Keep
import kotlin.Int
@Keep
data class offerDetailResponse(
    val brandName: String? = null,
    val code: String? = null,
    val tnc: String? = null,
    val offerImage: String? = null,
    val sortOrder: Int? = null,
    val name: String? = null,
    val brandShortTitle: String? = null,
    val id: Int? = null,
    val expiry: String? = null,
    val tncLink: Any? = null,
    val brandTitle: String? = null,
    val brandLogo: String? = null,
    val offerTitle: String? = null,
    val status: String? = null,

    val offerContent: String? = null,
    val rfu2: Any? = null,
    val rfu1: Any? = null,
    val rfu3: Any? = null,
    val offerShortTitle: String? = null,
    )


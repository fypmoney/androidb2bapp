package com.fypmoney.view.arcadegames.brandedcoupons.model

import androidx.annotation.Keep

@Keep
data class CouponDetailsTitleModel(
    var couponDetailsTitle: String,
    var couponDetailsIcon: Int,
    var arrayCouponDetails: List<String>,
    var isExpended:Boolean = false
)
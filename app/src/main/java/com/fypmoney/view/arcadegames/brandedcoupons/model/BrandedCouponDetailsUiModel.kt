package com.fypmoney.view.arcadegames.brandedcoupons.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class BrandedCouponDetailsUiModel(
    var howToRedeem: ArrayList<String>,
    var tnc: ArrayList<String>,
    var description: ArrayList<String>,
    var couponCode: String?,
    var brandLogo: String?,
    var couponTitle: String?,
    var startColor: String?,
    var endColor: String?,
    var redeemLink: String?,
    var brandType: String?,
    var revealX: Int?,
    var revealY: Int?,
    var via: String
) : Parcelable
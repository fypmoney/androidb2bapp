package com.fypmoney.view.recharge.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class MobileNumberInfoUiModel(
    var mobile:String?=null,
    var operator:String?=null,
    var circle:String?=null,
    var rechargeType:String?=null,
): Parcelable
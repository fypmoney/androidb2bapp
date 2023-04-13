package com.fypmoney.view.kycagent.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class SaveShopDetailsRequest(
    var agentContact1: String,
    var agentName: String,
    var shopName: String,
    var addr1: String,
    var state: String,
    var city: String,
    var pincode: String,
    var latitude: String,
    var longitude: String,
    var shopPhoto: String,
    var isPosterOrdered: String?
): Parcelable
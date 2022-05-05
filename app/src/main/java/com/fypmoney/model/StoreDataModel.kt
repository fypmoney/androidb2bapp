package com.fypmoney.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class StoreDataModel(
    val image: String? = null,
    var title: String? = null,
    var url: String? = null,
    var operator_id: String? = null,
    var Icon: Int? = null,
    var subscriberId:String? = null,
    var amount:String? = null
) : Parcelable


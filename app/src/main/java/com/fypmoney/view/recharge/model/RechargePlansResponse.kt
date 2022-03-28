package com.fypmoney.view.recharge.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class RechargePlansResponse(
    val name: String? = null,
    val value: List<ValueItem?>? = null
) : Parcelable

@Keep
@Parcelize
data class ValueItem(
    val rs: String? = null,
    val lastUpdate: String? = null,
    val validity: String? = null,
    val desc: String? = null
) : Parcelable


package com.fypmoney.view.recharge.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class OperatorResponse(
    var displayName: String? = null,
    var name: String? = null,
    var icon: String? = null,
    var id: String? = null,
    var category: String? = null,
    var type: String? = null,
    var operatorId: String? = null,
    var status: String? = null,
    var Icon: Int? = null

) : Parcelable


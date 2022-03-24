package com.fypmoney.view.recharge.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class OperatorResponse(
    val displayName: String? = null,
    val name: String? = null,
    val icon: String? = null,
    val id: String? = null,
    val category: String? = null,
    val type: String? = null,
    val operatorId: String? = null,
    val status: String? = null
) : Parcelable


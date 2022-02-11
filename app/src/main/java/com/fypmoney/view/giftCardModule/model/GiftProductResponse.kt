package com.fypmoney.view.giftCardModule.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize


@Keep
@Parcelize
data class GiftProductResponse(
    val validityInMonths: String? = null,
    val code: String? = null,
    val giftMessage: String? = null,
    val displayName: String? = null,
    val name: String? = null,
    val tnc: String? = null,
    val id: Int? = null,
    val tncLink: String? = null,
    val detailImage: String? = null,
    val voucherProduct: List<VoucherProductItem?>? = null
) : Parcelable

@Keep
@Parcelize
data class VoucherProductItem(
    val amount: Int? = null,
    val name: String? = null,
    val productGuid: String? = null,
    val tagline: String? = null,
    val maxAllowedQuantity: Int? = null,
    val id: Int? = null,
    val isFlexiblePrice: String? = null,
    val status: String? = null,
    var selected: Boolean? = false
) : Parcelable


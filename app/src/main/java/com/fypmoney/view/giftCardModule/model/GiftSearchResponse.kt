package com.fypmoney.view.giftCardModule.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class GiftSearchResponse(
    val code: String? = null,
    val name: String? = null,
    val description: String? = null,
    val voucherBrand: List<VoucherBrandItem?>? = null,
    val listImage: String? = null,
    val status: String? = null
) : Parcelable

@Keep
@Parcelize
data class VoucherBrandItem(
    val code: String? = null,
    val giftMessage: String? = null,
    val displayName: String? = null,
    val tnc: String? = null,
    val description: String? = null,
    val listImage: String? = null,
    val validityInMonths: String? = null,
    val name: String? = null,
    val tagline: String? = null,
    val discountPer: Int? = null,
    val id: Int? = null,
    val tncLink: String? = null,
    var selected: Boolean? = false,
    val detailImage: String? = null,
    val brandTagLine1: String? = null,
    val brandTagLine2: String? = null,
    val brandTagLine3: String? = null,
    val brandTagLine4: String? = null,
    val howToRedeem: String? = null,
    val brandTagLine5: String? = null,
    val brandLogo: String? = null,
    val fixedDenomiation: String? = null,
    val voucherProduct: List<VoucherProductItem>? = null,
    val successImage: String? = null,
    val minPrice: Int? = null,

    val maxPrice: Int? = null,
    val possibleDenominationList: String? = null
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
    var selected: Boolean? = false,

    ) : Parcelable

@Keep
@Parcelize
data class VoucherProductAmountsItem(

    var name: String? = null,

    var selected: Boolean? = false,

    ) : Parcelable
package com.fypmoney.view.giftCardModule.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class GiftHistoryResponseModel(
    val amount: Int? = null,
    val sourceUserName: String? = null,
    val endDate: String? = null,
    val tnc: String? = null,
    val productGuid: String? = null,
    val destinationEmail: String? = null,
    val voucherGcCode: String? = null,
    val externalOrderId: String? = null,
    val message: String? = null,
    val productName: String? = null,
    val voucherGuid: String? = null,
    val voucherNo: String? = null,
    val destinationUserName: String? = null,
    val destinationMobileNo: String? = null,
    val voucherName: String? = null,
    val id: Int? = null,
    val sourceUserId: Int? = null,
    val issueDate: String? = null,
    val tncLink: String? = null,
    val sourceMobileNo: String? = null,
    val isGifted: String? = null,
    val giftedPerson: String? = null,
    val destinationUserId: Int? = null,
    val detailImage: String? = null,


    val description: String? = null,

    val giftVoucherOrderNo: String? = null,

    val brandLogo: String? = null,

    val brandName: String? = null,

    val rfu2: String? = null,
    val rfu1: String? = null,
    val rfu3: String? = null,

    val isVoucherPurchased: String? = null,

    val voucherStatus: String? = null,


    ) : Parcelable


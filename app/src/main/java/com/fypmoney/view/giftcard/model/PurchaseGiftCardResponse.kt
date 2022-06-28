package com.fypmoney.view.giftcard.model

import androidx.annotation.Keep


@Keep
data class PurchaseGiftCardNetworkResponse(
    val data:PurchaseGiftCardResponse
)
@Keep
data class PurchaseGiftCardResponse(
    val msg: String? = null,
    val voucherGuid: String? = null,
    val voucherNo: String? = null,
    val endDate: String? = null,
    val voucherGcCode: String? = null,
    val detailImage: String? = null,
    val voucherOrderDetailId: String,
)
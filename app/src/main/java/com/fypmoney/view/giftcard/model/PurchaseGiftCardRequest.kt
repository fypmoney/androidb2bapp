package com.fypmoney.view.giftcard.model

import androidx.annotation.Keep

@Keep
data class PurchaseGiftCardRequest(
    var destinationMobileNo: String? = null,
    var destinationName: String? = null,
    var destinationEmail: String? = null,
    val message: String? = "",
    var voucherDetails: List<VoucherDetailsItem>,
    var giftedPerson: String? = null
)

@Keep
data class VoucherDetailsItem(
    var voucherProductId: String,
    var amount: String
)


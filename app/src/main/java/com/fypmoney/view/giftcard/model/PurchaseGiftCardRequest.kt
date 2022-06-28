package com.fypmoney.view.giftcard.model

data class PurchaseGiftCardRequest(
    var destinationMobileNo: String? = null,
    var destinationName: String? = null,
    var destinationEmail: String? = null,
    val message: String? = "",
    var voucherDetails: List<VoucherDetailsItem>,
    var giftedPerson: String? = null
)


data class VoucherDetailsItem(
    var voucherProductId: String,
    var amount: String
)


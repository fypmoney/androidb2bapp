package com.fypmoney.view.giftCardModule.model

data class PurchaseGiftCardRequest(
    var destinationMobileNo: String? = null,
    var destinationName: String? = null,
    var destinationEmail: String? = null,
    val message: String? = "",
    var voucherDetails: List<VoucherDetailsItem?>? = null,
    var giftedPerson: String? = null
)


data class VoucherDetailsItem(
    var voucherProductId: Int? = null
)


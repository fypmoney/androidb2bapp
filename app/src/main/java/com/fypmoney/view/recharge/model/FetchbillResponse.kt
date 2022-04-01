package com.fypmoney.view.recharge.model

data class FetchbillResponse(
    val amount: String? = null,
    val response_code: Int? = null,
    val duedate: String? = null,
    val name: String? = null,
    val bill_fetch: BillFetch? = null,
    val message: String? = null,
    val status: Boolean? = null
)

data class BillFetch(
    val billAmount: String? = null,
    val billnetamount: String? = null,
    val dueDate: String? = null,
    val billdate: String? = null,
    val acceptPartPay: Boolean? = null,
    val userName: String? = null,
    val acceptPayment: Boolean? = null,
    val cellNumber: String? = null
)


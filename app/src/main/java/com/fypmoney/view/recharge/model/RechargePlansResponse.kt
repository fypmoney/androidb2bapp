package com.fypmoney.view.recharge.model

data class RechargePlansResponse(
    val name: String? = null,
    val value: List<ValueItem?>? = null
)

data class ValueItem(
    val rs: String? = null,
    val lastUpdate: String? = null,
    val validity: String? = null,
    val desc: String? = null
)


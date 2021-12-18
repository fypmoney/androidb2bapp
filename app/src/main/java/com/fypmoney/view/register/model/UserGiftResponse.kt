package com.fypmoney.view.register.model
import androidx.annotation.Keep

@Keep
data class UserGiftResponse(
    val giftCode: String? = null,
    val giftResourceId: Any? = null,
    val name: String? = null,
    val giftType: String? = null,
    val value: Int? = null,
    val message: String? = null
)


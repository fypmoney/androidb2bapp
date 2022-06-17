package com.fypmoney.view.giftcard.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class CreateEGiftCardModel(
    var brandName:String,
    var voucherProductId:String,
    var amount:Long,
    var giftedPerson:String,
    var message:String? = "",
    var destinationMobileNo:String,
    var destinationEmail:String? = null,
    var destinationName:String,
):Parcelable


@Parcelize
@Keep
data class PurchasedGiftCardStatusUiModel(
    var amount:String,
    var message:String,
    var myntsEarned:String,
    var status:OrderStatus,
    var actionButtonVisibility:Boolean,
    var actionBtnText:String
)

sealed class OrderStatus{
    object Success:OrderStatus()
    object Failed:OrderStatus()
    object Pending:OrderStatus()
}


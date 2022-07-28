package com.fypmoney.view.addmoney.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class BankProfileDetailsNetworkResponse(

	@field:SerializedName("data")
	val data: List<BankDetailsItem?>? = null
)

@Keep
data class BankDetailsItem(

	@field:SerializedName("mode")
	val mode: String? = null,

	@field:SerializedName("identifier5")
	val identifier5: String? = null,

	@field:SerializedName("identifier1")
    var identifier1: String? = null,

	@field:SerializedName("identifier2")
	var identifier2: String? = null,

	@field:SerializedName("identifier3")
	val identifier3: String? = null,

	@field:SerializedName("identifier4")
	val identifier4: String? = null,

	@field:SerializedName("toShow")
    var toShow: String? = null
)

@Keep
data class BankDetailsUiModel(
	var loadMoneyMod:String,
	var modeVisibility:Visibility,
	var identifier1: String?,
	var identifier2: String?)

sealed class Visibility{
	object Visible:Visibility()
	object InVisible:Visibility()
}

@Keep
data class LoadMoneyWalletBalanceUIModel(
	var balance:String,
	var remainingLoadLimitTxt:String,
	var instructionOnBankTransfer:String,
	var instructionOnUpiQrCode:String
)
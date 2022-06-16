package com.fypmoney.view.giftcard.viewModel

import android.app.Application
import android.text.TextUtils
import androidx.annotation.Keep
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.BaseRequest
import com.fypmoney.model.GetWalletBalanceResponse
import com.fypmoney.util.Utility
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.giftcard.model.CreateGiftCardBrandNetworkResponse
import com.fypmoney.view.giftcard.model.GiftCardBrandDetails
import com.fypmoney.view.giftcard.model.PurchaseGiftCardRequest
import com.fypmoney.view.giftcard.model.PurchaseGiftCardResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

/*
* This class is used to handle add money functionality
* */
class CreateEGiftCardFragmentVM(application: Application) : BaseViewModel(application) {

    val state:LiveData<CreateEGiftCardState>
        get() = _state
    private var _state = MutableLiveData<CreateEGiftCardState>()

    val event:LiveData<CreateEGiftCardEvent>
        get() = _event
    private var _event = LiveEvent<CreateEGiftCardEvent>()

    var amountSelected = MutableLiveData<Int>()
    lateinit var brandDetails:GiftCardBrandDetails
    lateinit var brandCode:String

    fun onMySelfClicked(){

    }
    fun onSomeOneClicked(){

    }

    fun purchaseGiftCardRequest(orderId: PurchaseGiftCardRequest) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.PURCHASE_GIFT_CARD,
                NetworkUtil.endURL(ApiConstant.PURCHASE_GIFT_CARD),
                ApiUrl.POST,
                orderId,
                this, isProgressBar = true
            )
        )
    }

    fun getGiftCardBrandDetails(){
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_BRAND_DETAILS,
                NetworkUtil.endURL(ApiConstant.API_BRAND_DETAILS+brandCode),
                ApiUrl.GET,
                this,
                isProgressBar = true,
                onResponse = this
            )
        )
    }

    fun onAddClicked() {
        amountSelected.value?.let { amount->
            if(TextUtils.isEmpty(amount.toString())) {
                _state.value = CreateEGiftCardState.ValidationError(
                    ValidationErrorData(
                        field = Field.Amount,
                        validationMsg = PockketApplication.instance.getString(R.string.add_money_empty_error)
                    )
                )
            }else if(amount<Utility.convertToRs(brandDetails.minPrice.toString())!!.toInt()){
                _state.value = CreateEGiftCardState.ValidationError(
                    ValidationErrorData(
                        field = Field.Amount,
                        validationMsg = String.format(PockketApplication.instance.getString(
                            R.string.gift_card_amount_should_be_grater_with_min_amount),
                            String.format(PockketApplication.instance.getString(
                                R.string.amount_with_currency),brandDetails.minPrice)
                        )
                    )
                )
            }else if(amount>Utility.convertToRs(brandDetails.maxPrice.toString())!!.toInt()){
                _state.value = CreateEGiftCardState.ValidationError(
                    ValidationErrorData(
                        field = Field.Amount,
                        validationMsg = String.format(PockketApplication.instance.getString(
                            R.string.gift_card_amount_should_be_less_with_max_amount),
                            String.format(PockketApplication.instance.getString(
                                R.string.amount_with_currency),brandDetails.maxPrice)
                        )
                    )
                )
            }else{
                fetchBalance()
            }
        }

    }

    fun fetchBalance() {
        _state.postValue(CreateEGiftCardState.Loading)
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_GET_WALLET_BALANCE,
                NetworkUtil.endURL(ApiConstant.API_GET_WALLET_BALANCE),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }


    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.PURCHASE_GIFT_CARD -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject
                val obj = Gson().fromJson(
                    json.get("data"),
                    PurchaseGiftCardResponse::class.java
                )
            }
            ApiConstant.API_BRAND_DETAILS -> {
                if(responseData is CreateGiftCardBrandNetworkResponse){
                    brandDetails = responseData.data
                    _state.value = CreateEGiftCardState.BrandDetailsSuccess(responseData.data)
                    _state.value = CreateEGiftCardState.PossibleDenominationList(
                        responseData.data.possibleDenominationList?.split(",")?.toMutableList())
                }
            }
            ApiConstant.API_GET_WALLET_BALANCE -> {
                if (responseData is GetWalletBalanceResponse) {
                    responseData.getWalletBalanceResponseDetails.accountBalance.toIntOrNull()
                        ?.let { accountBalance ->
                            _state.value = CreateEGiftCardState.Success(accountBalance)
                            if(accountBalance < amountSelected.value?.toInt()!!){
                                _event.value = CreateEGiftCardEvent.ShowLowBalanceAlert(
                                    ((amountSelected.value?.toInt()!!) - (accountBalance)).toString())
                            }else{
                                _event.value = CreateEGiftCardEvent.ShowPaymentProcessingScreen
                            }
                        }
                }
            }

        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when (purpose) {
            ApiConstant.API_BRAND_DETAILS -> {
                _state.value = CreateEGiftCardState.Error
            }
            ApiConstant.API_GET_WALLET_BALANCE->{
                _state.value = CreateEGiftCardState.BalanceError
            }
        }
    }

    sealed class CreateEGiftCardState{
        object Loading:CreateEGiftCardState()
        object Error:CreateEGiftCardState()
        object BalanceError:CreateEGiftCardState()
        data class ValidationError(val validationError: ValidationErrorData):CreateEGiftCardState()
        data class BrandDetailsSuccess(val details:GiftCardBrandDetails?):CreateEGiftCardState()
        data class PossibleDenominationList(val list:MutableList<String>?):CreateEGiftCardState()
        data class Success(var balance:Int):CreateEGiftCardState()
    }

    sealed class CreateEGiftCardEvent{
        object OnSomeOneClickedEvent:CreateEGiftCardEvent()
        object OnPayClickedEvent:CreateEGiftCardEvent()
        data class ShowLowBalanceAlert(val amount:String?):CreateEGiftCardEvent()
        object ShowPaymentProcessingScreen:CreateEGiftCardEvent()
    }

    @Keep
    data class ValidationErrorData(var field:Field,var validationMsg:String)

    sealed class Field{
        object Amount:Field()
        object MobileNumber:Field()
    }
}



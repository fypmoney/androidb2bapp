package com.fypmoney.view.giftcard.viewModel

import android.app.Application
import android.text.TextUtils
import androidx.annotation.Keep
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
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
import com.fypmoney.view.giftcard.model.CreateEGiftCardModel
import com.fypmoney.view.giftcard.model.CreateGiftCardBrandNetworkResponse
import com.fypmoney.view.giftcard.model.GiftCardBrandDetails

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

    lateinit var brandDetails:GiftCardBrandDetails

    lateinit var brandCode:String
    val createEGiftCardModel:CreateEGiftCardModel = CreateEGiftCardModel(
        voucherProductId = "",
        amount = -1,
        giftedPerson = "",
        message = null,
        destinationMobileNo = "",
        destinationEmail = null,
        destinationName = "",
        brandName = "",
        myntsMultiPlier = ""
    )
    var giftCardForWhom:GiftCardForWhom = GiftCardForWhom.MySelf

    fun onMySelfClicked(){
        _state.value = CreateEGiftCardState.MySelfClickedState
    }
    fun onSomeOneClicked(){
        _state.value = CreateEGiftCardState.SomeOneClickedState
    }

    fun onPayClicked(){
        trackr {
            it.name = TrackrEvent.gift_card_pay
        }
        validation()
    }

    fun onSelectFromContactClicked(){
        _event.value = CreateEGiftCardEvent.OnSelectFromContactEvent
    }


    init {
        trackr {
            it.name = TrackrEvent.gift_card_select
        }
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

    private fun validation() {
        createEGiftCardModel.amount.let { amount->
            if(TextUtils.isEmpty(amount.toString()) || amount==-1L) {
                _state.value = CreateEGiftCardState.ValidationError(
                    ValidationErrorData(
                        field = Field.Amount,
                        validationMsg = PockketApplication.instance.getString(R.string.add_money_empty_error)
                    )
                )
            }
            else if(amount<Utility.convertToRs(brandDetails.minPrice.toString())!!.toLong()){
                _state.value = CreateEGiftCardState.ValidationError(
                    ValidationErrorData(
                        field = Field.Amount,
                        validationMsg = String.format(PockketApplication.instance.getString(
                            R.string.gift_card_amount_should_be_grater_with_min_amount),
                            String.format(PockketApplication.instance.getString(
                                R.string.amount_with_currency),Utility.convertToRs(brandDetails.minPrice.toString()))
                        )
                    )
                )
            }
            else if(amount>Utility.convertToRs(brandDetails.maxPrice.toString())!!.toLong()){
                _state.value = CreateEGiftCardState.ValidationError(
                    ValidationErrorData(
                        field = Field.Amount,
                        validationMsg = String.format(PockketApplication.instance.getString(
                            R.string.gift_card_amount_should_be_less_with_max_amount),
                            String.format(PockketApplication.instance.getString(
                                R.string.amount_with_currency),Utility.convertToRs(brandDetails.maxPrice.toString()))
                        )
                    )
                )
            }
            else if(giftCardForWhom==GiftCardForWhom.Someone){
                if(TextUtils.isEmpty(createEGiftCardModel.destinationName)){
                    _state.value = CreateEGiftCardState.ValidationError(
                        ValidationErrorData(
                            field = Field.Name,
                            validationMsg = PockketApplication.instance.getString(R.string.name_is_required)
                        )
                    )
                }else if(TextUtils.isEmpty(createEGiftCardModel.destinationMobileNo)){
                    _state.value = CreateEGiftCardState.ValidationError(
                        ValidationErrorData(
                            field = Field.MobileNumber,
                            validationMsg =  PockketApplication.instance.getString(R.string.mobile_no_is_required))
                    )
                }else{
                    checkForBalance()
                }
            }else{
                checkForBalance()
            }
        }
    }

    private fun checkForBalance() {
        when (giftCardForWhom) {
            GiftCardForWhom.MySelf -> {
                Utility.getCustomerDataFromPreference()?.mobile?.let {
                    createEGiftCardModel.destinationMobileNo = it
                }
                createEGiftCardModel.giftedPerson = "MYSELF"
            }
            GiftCardForWhom.Someone -> {
                createEGiftCardModel.giftedPerson = "NONE"
            }
        }
        brandDetails.displayName?.let {
            createEGiftCardModel.brandName = it

        }
        brandDetails.voucherProduct?.let {
            if(it.isNotEmpty()){
                createEGiftCardModel.voucherProductId = it[0]?.id.toString()
            }

        }
        createEGiftCardModel.myntsMultiPlier = brandDetails.myntsMultiPlier
        fetchBalance()
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
                            if(accountBalance < Utility.convertToPaise(createEGiftCardModel.amount.toString())?.toLong()!!){
                                _event.value = CreateEGiftCardEvent.ShowLowBalanceAlert(
                                    ((Utility.convertToPaise(createEGiftCardModel.amount.toString())?.toLong()!!) - (accountBalance)).toString())
                            }else{
                                _event.value = CreateEGiftCardEvent.ShowPaymentProcessingScreen(createEGiftCardModel)
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
        object MySelfClickedState:CreateEGiftCardState()
        object SomeOneClickedState:CreateEGiftCardState()
    }

    sealed class CreateEGiftCardEvent{
        object OnPayClickedEvent:CreateEGiftCardEvent()
        data class ShowLowBalanceAlert(val amount:String?):CreateEGiftCardEvent()
        data class ShowPaymentProcessingScreen(val createEGiftCardModel: CreateEGiftCardModel):CreateEGiftCardEvent()
        object OnSelectFromContactEvent:CreateEGiftCardEvent()
    }


    @Keep
    data class ValidationErrorData(var field: Field, var validationMsg:String)

    sealed class Field{
        object Amount:Field()
        object Name:Field()
        object MobileNumber:Field()
    }
    sealed class GiftCardForWhom{
        object MySelf:GiftCardForWhom()
        object Someone:GiftCardForWhom()
    }
}




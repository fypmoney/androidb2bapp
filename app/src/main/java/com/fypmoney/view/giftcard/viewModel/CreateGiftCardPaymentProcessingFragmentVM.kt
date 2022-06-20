package com.fypmoney.view.giftcard.viewModel

import android.app.Application
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
import com.fypmoney.util.Utility
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.giftcard.model.*

class CreateGiftCardPaymentProcessingFragmentVM(application: Application) : BaseViewModel(application) {

    lateinit var createEGiftCardModel:CreateEGiftCardModel

    val state:LiveData<CreateGiftCardPaymentProcessingState>
        get() = _state
    private val _state = MutableLiveData<CreateGiftCardPaymentProcessingState>()

    val event:LiveData<CreateGiftCardPaymentProcessingEvent>
        get() = _event
    private val _event = LiveEvent<CreateGiftCardPaymentProcessingEvent>()


    fun updateText(){
       _state.value = CreateGiftCardPaymentProcessingState.UpdateText(
           String.format(
               PockketApplication.instance.getString(R.string.processing_creating_gift_card),
               createEGiftCardModel.amount, createEGiftCardModel.brandName
           )
       )
    }

    fun purchaseGiftCardRequest() {
        _state.postValue(CreateGiftCardPaymentProcessingState.Loading)
        val voucherItem = VoucherDetailsItem(voucherProductId = createEGiftCardModel.voucherProductId,
            amount = Utility.convertToRs(createEGiftCardModel.amount.toString())!!)
        val voucherItemList = arrayListOf<VoucherDetailsItem>()
        voucherItemList.add(voucherItem)

        val purchaseGiftCardRequest = PurchaseGiftCardRequest(
            destinationMobileNo = createEGiftCardModel.destinationMobileNo,
            destinationName = createEGiftCardModel.destinationName,
            destinationEmail = createEGiftCardModel.destinationEmail,
            message = "",
            voucherDetails = voucherItemList,
            giftedPerson = createEGiftCardModel.giftedPerson
        )

        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.PURCHASE_GIFT_CARD,
                NetworkUtil.endURL(ApiConstant.PURCHASE_GIFT_CARD),
                ApiUrl.POST,
                purchaseGiftCardRequest,
                onResponse = this, isProgressBar = false
            )
        )
    }



    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when(purpose){
            ApiConstant.PURCHASE_GIFT_CARD -> {
                if(responseData is PurchaseGiftCardResponse){
                    _event.value = CreateGiftCardPaymentProcessingEvent.NavigateToStatusScreen(
                        PurchasedGiftCardStatusUiModel(
                            purchaseGiftCardDetailId = responseData.voucherOrderDetailId,
                            amount = createEGiftCardModel.amount.toString(),
                            title = "",
                            subTitle = if(responseData.voucherNo.isNullOrEmpty()) PockketApplication.instance.getString(R.string.any_amount_detucetd) else "",
                            myntsEarned = "24",
                            myntsVisibility = !responseData.voucherNo.isNullOrEmpty(),
                            statusAnimRes = if(responseData.voucherNo.isNullOrEmpty()) R.raw.pending else R.raw.success,
                            status = if(responseData.voucherNo.isNullOrEmpty())  CreateEGiftCardOrderStatus.Pending else CreateEGiftCardOrderStatus.Success,
                            actionButtonVisibility = responseData.voucherNo.isNullOrEmpty(),
                            actionBtnText = if(responseData.voucherNo.isNullOrEmpty())  PockketApplication.instance.getString(R.string.continue_btn_text) else PockketApplication.instance.getString(R.string.continue_btn_text),
                            actionBtnCTA = if(responseData.voucherNo.isNullOrEmpty())   GiftCardStatusScreenCTA.NavigateToHome else GiftCardStatusScreenCTA.NavigateToGiftCardDetails(responseData.voucherOrderDetailId)
                        )
                    )
                }
            }
        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when(purpose){
            ApiConstant.PURCHASE_GIFT_CARD -> {
                //_state.value = CreateGiftCardPaymentProcessingState.Error
                _event.value = CreateGiftCardPaymentProcessingEvent.NavigateToStatusScreen(
                    PurchasedGiftCardStatusUiModel(
                        purchaseGiftCardDetailId = "",
                        amount = createEGiftCardModel.amount.toString(),
                        title = "",
                        subTitle =  PockketApplication.instance.getString(R.string.any_amount_detucetd),
                        myntsEarned = "24",
                        myntsVisibility = false,
                        statusAnimRes =  R.raw.failed,
                        status =  CreateEGiftCardOrderStatus.Failed ,
                        actionButtonVisibility = true,
                        actionBtnText =  PockketApplication.instance.getString(R.string.retry_btn_text),
                        actionBtnCTA =   GiftCardStatusScreenCTA.NavigateToHome
                    )
                )
            }
        }
    }

    sealed class CreateGiftCardPaymentProcessingState{
        object Loading: CreateGiftCardPaymentProcessingState()
        object Error: CreateGiftCardPaymentProcessingState()
        data class UpdateText(val title:String): CreateGiftCardPaymentProcessingState()
    }
    sealed class CreateGiftCardPaymentProcessingEvent{
        data class NavigateToStatusScreen(var purchasedGiftCardStatusUiModel: PurchasedGiftCardStatusUiModel):CreateGiftCardPaymentProcessingEvent()
    }
}
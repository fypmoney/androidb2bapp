package com.fypmoney.view.giftcard.viewModel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.fypmoney.base.BaseViewModel
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.giftcard.model.CreateEGiftCardOrderStatus
import com.fypmoney.view.giftcard.model.GiftCardStatusScreenCTA
import com.fypmoney.view.giftcard.model.PurchasedGiftCardStatusUiModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CreateGiftCardPaymentStatusFragmentVM(application: Application) : BaseViewModel(application) {
    lateinit var purchasedGiftCardStatusUiModel: PurchasedGiftCardStatusUiModel

    val state:LiveData<CreateGiftCardPaymentSuccessState>
        get() = _state
    private val _state = LiveEvent<CreateGiftCardPaymentSuccessState>()


    val event:LiveData<CreateGiftCardPaymentSuccessEvent>
        get() = _event
    private val _event = LiveEvent<CreateGiftCardPaymentSuccessEvent>()

    fun onContinueClick(){
        checkForNavigation()
    }
    fun checkForStatus(){
        when(purchasedGiftCardStatusUiModel.status){
            CreateEGiftCardOrderStatus.Failed -> {
                _state.value =
                    CreateGiftCardPaymentSuccessState.Failed(purchasedGiftCardStatusUiModel)
            }
            CreateEGiftCardOrderStatus.Pending -> {
                _state.value =
                    CreateGiftCardPaymentSuccessState.Pending(purchasedGiftCardStatusUiModel)

            }
            CreateEGiftCardOrderStatus.Success -> {
                _state.value =
                    CreateGiftCardPaymentSuccessState.Success(purchasedGiftCardStatusUiModel)
                viewModelScope.launch {
                    delay(2000)
                    checkForNavigation()
                }
            }
        }
    }

    private fun checkForNavigation() {
        when(purchasedGiftCardStatusUiModel.actionBtnCTA){
            GiftCardStatusScreenCTA.NavigateToHome -> {
                _event.value = CreateGiftCardPaymentSuccessEvent.NavigateToHome
            }
            is GiftCardStatusScreenCTA.NavigateToGiftCardDetails -> {
                _event.value = CreateGiftCardPaymentSuccessEvent.NavigateToGiftCardDetails(
                    purchasedGiftCardStatusUiModel.purchaseGiftCardDetailId
                )
            }
        }
    }

    sealed class CreateGiftCardPaymentSuccessState{
        data class Success(val purchasedGiftCardStatusUiModel: PurchasedGiftCardStatusUiModel):
            CreateGiftCardPaymentSuccessState()
        data class Failed(val purchasedGiftCardStatusUiModel: PurchasedGiftCardStatusUiModel):
            CreateGiftCardPaymentSuccessState()
        data class Pending(val purchasedGiftCardStatusUiModel: PurchasedGiftCardStatusUiModel):
            CreateGiftCardPaymentSuccessState()
    }
    sealed class CreateGiftCardPaymentSuccessEvent{
        object NavigateToHome: CreateGiftCardPaymentSuccessEvent()
        data class NavigateToGiftCardDetails(var giftCardId: String) :
            CreateGiftCardPaymentSuccessEvent()
    }
}
package com.fypmoney.view.card.viewmodel

import android.app.Application
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.TrackrField
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
import com.fypmoney.model.*
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.card.model.CardOptionEvent
import com.fypmoney.view.card.model.CardOptionUiModel
import com.fypmoney.view.fragment.*
import org.json.JSONObject

class CardFragmentVM(application: Application) :
    BaseViewModel(application),
    CardSettingsBottomSheet.OnCardSettingsClickListener,
    ManageChannelsBottomSheet.OnBottomSheetDismissListener,
    SetSpendingLimitBottomSheet.OnSetSpendingLimitClickListener,
    SetOrChangePinBottomSheet.OnSetOrChangePinClickListener,
    ActivateCardBottomSheet.OnActivateCardClickListener {

    var bankProfile: BankProfileResponseDetails? = null

    var virtualCardDetails: FetchVirtualCardResponseDetails? = null

    var isCvvVisible: Boolean = false

    val state: LiveData<CardState>
        get() = _state
    private val _state = MutableLiveData<CardState>()

    val event: LiveData<CardEvent>
        get() = _event
    private val _event = LiveEvent<CardEvent>()

    init {
        callGetVirtualRequestApi()
    }

    private fun callGetVirtualRequestApi() {
        _state.value = CardState.LoadingCardDetails
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_GET_VIRTUAL_CARD_REQUEST,
                NetworkUtil.endURL(ApiConstant.API_GET_VIRTUAL_CARD_REQUEST),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = true
            )
        )
    }

    fun prepareInitialCardOption() {
        val cardOptionList = mutableListOf<CardOptionUiModel>()
        cardOptionList.add(
            CardOptionUiModel(
                optionEvent = CardOptionEvent.OrderCard,
                icon = AppCompatResources.getDrawable(
                    PockketApplication.instance,
                    R.drawable.ic_order_card
                ),
                name = PockketApplication.instance.getString(R.string.order_card)
            )
        )
        cardOptionList.add(
            CardOptionUiModel(
                optionEvent = CardOptionEvent.CardSettings,
                icon = AppCompatResources.getDrawable(
                    PockketApplication.instance,
                    R.drawable.ic_card_settings
                ),
                name = PockketApplication.instance.getString(R.string.card_settings)
            )
        )
        cardOptionList.add(
            CardOptionUiModel(
                optionEvent = CardOptionEvent.CardSettings,
                icon = AppCompatResources.getDrawable(
                    PockketApplication.instance,
                    R.drawable.ic_account_statement
                ),
                name = PockketApplication.instance.getString(R.string.account_stmt)
            )
        )
        _state.value = CardState.CardOptionState(cardOptionList)
    }

    private fun prepareTrackCardOption() {
        val cardOptionList = mutableListOf<CardOptionUiModel>()
        cardOptionList.add(
            CardOptionUiModel(
                optionEvent = CardOptionEvent.CardSettings,
                icon = AppCompatResources.getDrawable(
                    PockketApplication.instance,
                    R.drawable.ic_card_settings
                ),
                name = PockketApplication.instance.getString(R.string.card_settings)
            )
        )
        cardOptionList.add(
            CardOptionUiModel(
                optionEvent = CardOptionEvent.TrackOrder,
                icon = AppCompatResources.getDrawable(
                    PockketApplication.instance,
                    R.drawable.ic_order_card
                ),
                name = PockketApplication.instance.getString(R.string.track_order)
            )
        )
        cardOptionList.add(
            CardOptionUiModel(
                optionEvent = CardOptionEvent.AccountStatement,
                icon = AppCompatResources.getDrawable(
                    PockketApplication.instance,
                    R.drawable.ic_account_statement
                ),
                name = PockketApplication.instance.getString(R.string.account_stmt)
            )
        )
        _state.value = CardState.CardOptionState(cardOptionList)
    }

    private fun prepareActivateCardOption() {
        val cardOptionList = mutableListOf<CardOptionUiModel>()
        cardOptionList.add(
            CardOptionUiModel(
                optionEvent = CardOptionEvent.ActivateCard,
                icon = AppCompatResources.getDrawable(
                    PockketApplication.instance,
                    R.drawable.ic_activate
                ),
                name = PockketApplication.instance.getString(R.string.activate_card_heading)
            )
        )
        cardOptionList.add(
            CardOptionUiModel(
                optionEvent = CardOptionEvent.CardSettings,
                icon = AppCompatResources.getDrawable(
                    PockketApplication.instance,
                    R.drawable.ic_card_settings
                ),
                name = PockketApplication.instance.getString(R.string.card_settings)
            )
        )
        cardOptionList.add(
            CardOptionUiModel(
                optionEvent = CardOptionEvent.TrackOrder,
                icon = AppCompatResources.getDrawable(
                    PockketApplication.instance,
                    R.drawable.ic_order_card
                ),
                name = PockketApplication.instance.getString(R.string.track_order)
            )
        )
        cardOptionList.add(
            CardOptionUiModel(
                optionEvent = CardOptionEvent.AccountStatement,
                icon = AppCompatResources.getDrawable(
                    PockketApplication.instance,
                    R.drawable.ic_account_statement
                ),
                name = PockketApplication.instance.getString(R.string.account_stmt)
            )
        )
        _state.value = CardState.CardOptionState(cardOptionList)
    }

    private fun prepareEnabledCardOption() {
        val cardOptionList = mutableListOf<CardOptionUiModel>()
        cardOptionList.add(
            CardOptionUiModel(
                optionEvent = CardOptionEvent.CardSettings,
                icon = AppCompatResources.getDrawable(
                    PockketApplication.instance,
                    R.drawable.ic_card_settings
                ),
                name = PockketApplication.instance.getString(R.string.card_settings)
            )
        )
        cardOptionList.add(
            CardOptionUiModel(
                optionEvent = CardOptionEvent.AccountStatement,
                icon = AppCompatResources.getDrawable(
                    PockketApplication.instance,
                    R.drawable.ic_account_statement
                ),
                name = PockketApplication.instance.getString(R.string.account_stmt)
            )
        )
        _state.value = CardState.CardOptionState(cardOptionList)
    }


    fun onFetchCardDetails() {
        callGetVirtualRequestApi()
    }

    fun onCopyCardNumber() {
        _event.value = CardEvent.OnCardNumberCopyEvent(virtualCardDetails?.card_number)
    }

    fun onCvvEyeClicked() {
        _event.value = CardEvent.OnCVVClicked(!isCvvVisible, virtualCardDetails?.cvv)
        isCvvVisible = !isCvvVisible
    }

    fun onCardOptionClicked(model: CardOptionUiModel) {
        when (model.optionEvent) {
            CardOptionEvent.AccountStatement -> {
                _event.value = CardEvent.AccountStatementEvent
            }
            CardOptionEvent.ActivateCard -> {
                _event.value = CardEvent.ActivateCardEvent(this)
            }
            CardOptionEvent.CardSettings -> {
                _event.value = CardEvent.CardSettingsEvent
            }
            CardOptionEvent.OrderCard -> {
                trackr {
                    it.name = TrackrEvent.ordered_card
                    it.add(
                        TrackrField.user_id, SharedPrefUtils.getLong(
                            PockketApplication.instance,
                            SharedPrefUtils.SF_KEY_USER_ID
                        ).toString()
                    )
                }
                _event.value = CardEvent.OrderCardEvent
            }
            CardOptionEvent.TrackOrder -> {
                SharedPrefUtils.getString(
                    PockketApplication.instance,
                    SharedPrefUtils.SF_KEY_KIT_NUMBER
                ).let {
                    if (!it.isNullOrEmpty()) {
                        _event.value = CardEvent.TrackOrderEvent
                    }
                }
            }
        }
    }

    fun callGetBankProfileApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_GET_BANK_PROFILE,
                NetworkUtil.endURL(ApiConstant.API_GET_BANK_PROFILE),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = true
            )
        )
    }


    private fun callUpdateCardLimitApi(updateCardLimitRequest: UpdateCardLimitRequest) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_UPDATE_CARD_LIMIT,
                NetworkUtil.endURL(ApiConstant.API_UPDATE_CARD_LIMIT),
                ApiUrl.PUT,
                updateCardLimitRequest,
                this, isProgressBar = true
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_GET_BANK_PROFILE -> {
                if (responseData is BankProfileResponse) {
                    bankProfile = responseData.bankProfileResponseDetails
                    _state.value =
                        CardState.BankProfilePopulate(responseData.bankProfileResponseDetails)
                    saveKitNumberInPref()
                    updateCardOptions(responseData.bankProfileResponseDetails?.cardInfos)
                }
            }
            ApiConstant.API_UPDATE_CARD_LIMIT -> {
                if (responseData is UpdateCardLimitResponse) {
                    callGetBankProfileApi()
                    Utility.showToast(responseData.msg)
                }

            }

            ApiConstant.API_SET_CHANGE_PIN -> {
                if (responseData is SetPinResponse) {
                    trackr { it1 ->
                        it1.name = TrackrEvent.pin_success
                    }
                    _state.value =
                        CardState.SetPinSuccessState(responseData.setPinResponseDetails!!)
                }
            }
            ApiConstant.API_ACTIVATE_CARD -> {
                if (responseData is ActivateCardResponse) {
                    trackr { it1 ->
                        it1.name = TrackrEvent.card_activate_success
                    }
                    callGetBankProfileApi()
                    Utility.showToast(responseData.activateCardResponseDetails?.message)
                    _event.value = CardEvent.SetPinDialogEvent(setPinClickListener = {
                        callSetOrChangeApi()
                    })
                }
            }

            ApiConstant.API_GET_VIRTUAL_CARD_REQUEST -> {
                if (responseData is VirtualCardRequestResponse) {
                    callGetVirtualCardDetailsApi(makeFetchCardRequest(responseData.virtualCardRequestResponseDetails.requestData))
                }
            }
            ApiConstant.API_FETCH_VIRTUAL_CARD_DETAILS -> {
                if (responseData is FetchVirtualCardResponse) {
                    virtualCardDetails = responseData.fetchVirtualCardResponseDetails
                    _state.value =
                        CardState.VirtualCardDetails(responseData.fetchVirtualCardResponseDetails)
                }
            }
        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when (purpose) {
            ApiConstant.API_ACTIVATE_CARD -> {
                Utility.showToast(errorResponseInfo.msg)
            }
        }
        _state.value = CardState.Error(purpose, errorResponseInfo)

    }

    private fun makeFetchCardRequest(requestData: String): FetchVirtualCardRequest {
        val fetchVirtualCardRequest = FetchVirtualCardRequest()
        val mainObject = JSONObject(requestData)
        fetchVirtualCardRequest.action_name = mainObject.getString("action_name")
        fetchVirtualCardRequest.wlap_secret_key = mainObject.getString("wlap_secret_key")
        fetchVirtualCardRequest.wlap_code = mainObject.getString("wlap_code")
        fetchVirtualCardRequest.p1 = mainObject.getString("p1")
        fetchVirtualCardRequest.p2 = mainObject.getString("p2")
        fetchVirtualCardRequest.checksum = mainObject.getString("checksum")
        return fetchVirtualCardRequest

    }

    private fun callGetVirtualCardDetailsApi(fetchVirtualCardRequest: FetchVirtualCardRequest) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_FETCH_VIRTUAL_CARD_DETAILS,
                NetworkUtil.endURL(ApiConstant.API_FETCH_VIRTUAL_CARD_DETAILS),
                ApiUrl.POST,
                fetchVirtualCardRequest,
                this, isProgressBar = true
            )
        )
    }

    private fun saveKitNumberInPref() {
        bankProfile?.let {
            it.cardInfos?.forEach { cardInfo ->
                when (cardInfo.cardType) {
                    AppConstants.CARD_TYPE_PHYSICAL -> {
                        SharedPrefUtils.putString(
                            getApplication(),
                            SharedPrefUtils.SF_KEY_KIT_NUMBER, cardInfo.kitNumber
                        )
                    }
                }
            }
        }
    }

    private fun updateCardOptions(cardInfos: List<CardInfoDetails>?) {
        if (cardInfos != null) {
            for (cardInfo in cardInfos) {
                when (cardInfo.cardType) {
                    AppConstants.CARD_TYPE_PHYSICAL -> {
                        if (cardInfo.kitNumber.isNullOrEmpty()) {
                            prepareInitialCardOption()
                        }
                        if (!cardInfo.kitNumber.isNullOrEmpty()) {
                            prepareTrackCardOption()
                        }
                        if (cardInfo.isCardActivationAllowed == AppConstants.YES) {
                            prepareActivateCardOption()
                        }
                        if (cardInfo.status == AppConstants.ENABLE) {
                            prepareEnabledCardOption()
                        }
                    }
                }
            }

        }
    }


    override fun onCardSettingsClick(position: Int) {
        when (position) {
            0 -> {
                _event.value = CardEvent.BlockUnblockEvent(
                    bankProfile, this
                )
            }
            1 -> {
                _event.value = CardEvent.SetCardLimitEvent(
                    bankProfile,
                    this, this
                )
            }
            2 -> {
                _event.value = CardEvent.ManageChannelEvent(
                    bankProfile, this
                )
            }
            3 -> {
                _event.value = CardEvent.SetPinEvent(
                    this
                )
            }
        }
    }

    override fun onBottomSheetDismiss() {
        callGetBankProfileApi()
    }

    override fun onSetSpendingLimitButtonClick(updateCardLimitRequest: UpdateCardLimitRequest) {
        callUpdateCardLimitApi(updateCardLimitRequest)
    }

    override fun setPinClick() {
        callSetOrChangeApi()
    }

    fun callSetOrChangeApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_SET_CHANGE_PIN,
                NetworkUtil.endURL(
                    ApiConstant.API_SET_CHANGE_PIN + SharedPrefUtils.getString(
                        getApplication(),
                        SharedPrefUtils.SF_KEY_KIT_NUMBER
                    )
                ),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = true
            )
        )
    }

    override fun onActivateCardClick(kitFourDigit: String?) {
        callActivateCardApi(kitFourDigit)

    }

    private fun callActivateCardApi(kitFourDigit: String?) {
        val additionalInfo = System.currentTimeMillis().toString()
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_ACTIVATE_CARD,
                NetworkUtil.endURL(ApiConstant.API_ACTIVATE_CARD),
                ApiUrl.POST,
                ActivateCardRequest(
                    validationNo = kitFourDigit,
                    additionalInfo = additionalInfo,
                    cardIdentifier = SharedPrefUtils.getString(
                        getApplication(),
                        SharedPrefUtils.SF_KEY_KIT_NUMBER
                    )
                ),
                this, isProgressBar = true
            )
        )
    }

    override fun onPrivacyPolicyTermsClicked(title: String, url: String) {
        _event.value = CardEvent.PrivacyPolicyTermsConditionEvent(title, url)
    }

    sealed class CardEvent {
        data class OnCVVClicked(var isCvvVisible: Boolean, var cvvValue: String?) : CardEvent()
        object CardSettingsEvent : CardEvent()
        object OrderCardEvent : CardEvent()
        object TrackOrderEvent : CardEvent()
        object AccountStatementEvent : CardEvent()
        data class ActivateCardEvent(
            var onActivateCardClickListener
            : ActivateCardBottomSheet.OnActivateCardClickListener
        ) : CardEvent()

        data class BlockUnblockEvent(
            var bankProfile: BankProfileResponseDetails?,
            var bottomSheetDismissListener: ManageChannelsBottomSheet.
            OnBottomSheetDismissListener
        ) : CardEvent()

        data class SetCardLimitEvent(
            var bankProfile: BankProfileResponseDetails?,
            var onSetSpendingLimitClickListener:
            SetSpendingLimitBottomSheet.OnSetSpendingLimitClickListener,
            var bottomSheetDismissListener:
            ManageChannelsBottomSheet.OnBottomSheetDismissListener

        ) : CardEvent()

        data class ManageChannelEvent(
            var bankProfile: BankProfileResponseDetails?,
            var bottomSheetDismissListener: ManageChannelsBottomSheet.
            OnBottomSheetDismissListener
        ) : CardEvent()

        data class SetPinEvent(
            var setOrChangePinListener:
            SetOrChangePinBottomSheet.OnSetOrChangePinClickListener
        ) : CardEvent()

        data class SetPinDialogEvent(var setPinClickListener: () -> Unit) : CardEvent()
        data class PrivacyPolicyTermsConditionEvent(
            var title: String,
            var url: String,
        ) : CardEvent()

        data class OnCardNumberCopyEvent(
            var cardNumber: String?
        ) : CardEvent()

    }

    sealed class CardState {
        object Loading : CardState()
        object LoadingCardDetails : CardState()

        data class BankProfilePopulate(
            var bankProfile: BankProfileResponseDetails?
        ) : CardState()

        data class Error(
            var purpose: String,
            var error: ErrorResponseInfo
        ) : CardState()

        data class CardOptionState(
            var listOfOption: List<CardOptionUiModel>
        ) : CardState()

        data class SetPinSuccessState(
            var setPinResponseDetails: SetPinResponseDetails
        ) : CardState()

        data class VirtualCardDetails(
            var virtualCardDetails: FetchVirtualCardResponseDetails
        ) : CardState()

    }

}
package com.fypmoney.viewmodel

 import android.app.Application
 import android.text.TextUtils
 import androidx.databinding.ObservableField
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

/*
* This class is used to handle add money functionality
* */
class AddMoneyViewModel(application: Application) : BaseViewModel(application) {
    var onAddClicked = MutableLiveData(false)
    var maxLoadLimitReached = MutableLiveData(false)
    var setEdittextLength = MutableLiveData(false)
    var availableAmount = ObservableField(application.getString(R.string.dummy_amount))
    var amountSelected = ObservableField<String>("1000") //prefilled amount
    var isFetchBalanceVisible = ObservableField(true)
    var isFetchingBalanceTextVisible = ObservableField(true)
    var apiFail = ObservableField(true)
    var remainingLoadLimit = ObservableField<String>()
    var remainingLoadLimitAmount = ObservableField<String>()
    var increseLimitClicked = MutableLiveData(false)
    var enableButton = MutableLiveData(false)



    init {
        callGetWalletBalanceApi()
    }

    /*
      * This method is used to handle on click of add
      * */
    fun onAddClicked() {
        amountSelected.get()?.toIntOrNull()?.let {
            when {
                TextUtils.isEmpty(amountSelected.get()) -> {
                    Utility.showToast(PockketApplication.instance.getString(R.string.add_money_empty_error))
                }
                it > 5000 -> {
                    Utility.showToast(PockketApplication.instance.getString(R.string.maximum_load_amount))
                }
                //TODO remove comment at the time of release
                /*it < 50 -> {
                    Utility.showToast(PockketApplication.instance.getString(R.string.minimum_load_amount))
                }*/
                remainingLoadLimitAmount.get() != null && it>remainingLoadLimitAmount.get()!!.toInt()->{
                    maxLoadLimitReached.value = true
                }
                else -> {
                    onAddClicked.value = true
                }
            }
        }

    }

    fun onIncreaseLimitClicked(){
        increseLimitClicked.value = true
    }

    fun onAmountSelected(amount: Int) {
        amountSelected.set(amount.toString())
        setEdittextLength.value = true
    }

    /*
       * This method is used to get the balance of wallet
       * */
    fun callGetWalletBalanceApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_GET_WALLET_BALANCE,
                NetworkUtil.endURL(ApiConstant.API_GET_WALLET_BALANCE),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = true
            )
        )
    }
    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_GET_WALLET_BALANCE -> {

                if (responseData is GetWalletBalanceResponse) {
                    enableButton.value = true
                    apiFail.set(true)
                    isFetchBalanceVisible.set(false)
                    isFetchingBalanceTextVisible.set(false)
                    availableAmount.set(Utility.getFormatedAmount(Utility.convertToRs(responseData.getWalletBalanceResponseDetails.accountBalance)!!))
                    remainingLoadLimitAmount.set(responseData.getWalletBalanceResponseDetails.remainingLoadLimit)
                    remainingLoadLimit.set(
                        PockketApplication.instance.getString(R.string.add_money_screen_text) + Utility.convertToRs(
                            responseData.getWalletBalanceResponseDetails.remainingLoadLimit
                        ) + PockketApplication.instance.getString(R.string.add_money_screen_text1)
                    )
                }
            }

        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when (purpose) {
            ApiConstant.API_GET_WALLET_BALANCE -> {
                isFetchBalanceVisible.set(true)
                isFetchingBalanceTextVisible.set(false)
                apiFail.set(false)
            }
        }

    }

    }



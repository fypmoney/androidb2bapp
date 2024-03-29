package com.fypmoney.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiConstant.API_FETCH_BILL
import com.fypmoney.connectivity.ApiConstant.API_GET_CIRCLE_LIST
import com.fypmoney.connectivity.ApiConstant.API_GET_HLR_CHECK
import com.fypmoney.connectivity.ApiConstant.API_GET_OPERATOR_LIST_MOBILE
import com.fypmoney.connectivity.ApiConstant.API_GET_WALLET_BALANCE
import com.fypmoney.connectivity.ApiConstant.API_MOBILE_RECHARGE
import com.fypmoney.connectivity.ApiConstant.API_PAY_BILL
import com.fypmoney.connectivity.ApiConstant.API_RECENT_RECHARGE
import com.fypmoney.connectivity.ApiConstant.API_RECHARGE_PLANS
import com.fypmoney.connectivity.ApiConstant.PLAY_ORDER_API
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.database.AppDatabase
import com.fypmoney.util.DialogUtils
import com.fypmoney.util.Utility
import com.fypmoney.util.livedata.LiveEvent

/**
 *Base View Model class
 */

abstract class BaseViewModel(application: Application) : AndroidViewModel(application),
    WebApiCaller.OnWebApiResponse {
    var progressDialog = MutableLiveData(false)
    val internetError:LiveEvent<Boolean> = LiveEvent()
    val logoutUser:LiveEvent<Boolean> = LiveEvent()
    var appDatabase: AppDatabase? = null
    val alertDialog = MutableLiveData<DialogUtils.AlertStateUiModel>()

    init {
        appDatabase = AppDatabase.getInstance()
    }

    override fun onSuccess(purpose: String, responseData: Any) {
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        when (errorResponseInfo.errorCode) {
            "101" -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.please_try_again_after_some_time))

            }
            "102" -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.please_try_again_after_some_time))

            }
            "401" -> {
                logoutUser.value = true
            }
            else -> {
                try {
                    when {
                        purpose != ApiConstant.API_SNC_CONTACTS ||
                                purpose != ApiConstant.API_FETCH_ALL_FEEDS ||
                                purpose != ApiConstant.API_GET_CUSTOMER_INFO ||
                                purpose != ApiConstant.API_ADD_FAMILY_MEMBER ||
                                purpose != ApiConstant.API_GET_VIRTUAL_CARD_REQUEST ||
                                purpose != ApiConstant.API_ADD_MONEY_STEP2 ||
                                purpose != ApiConstant.API_LOGOUT ||
                                purpose != ApiConstant.API_REDEEM_REWARD ||
                                purpose != PLAY_ORDER_API ||
                                purpose != API_GET_WALLET_BALANCE ||
                                purpose!=API_RECHARGE_PLANS||
                                purpose!=API_GET_OPERATOR_LIST_MOBILE||
                                purpose!=API_GET_CIRCLE_LIST||
                                purpose!=API_GET_HLR_CHECK||
                                purpose!=API_FETCH_BILL||
                                purpose!=API_MOBILE_RECHARGE||
                                purpose!=API_PAY_BILL||
                                purpose!=API_RECENT_RECHARGE-> {
                            Utility.showToast(errorResponseInfo.msg)

                        }
                    }
                } catch (e: Exception) {
                    when {
                        purpose != ApiConstant.API_SNC_CONTACTS || purpose != ApiConstant.API_ADD_FAMILY_MEMBER || purpose != ApiConstant.API_GET_VIRTUAL_CARD_REQUEST || purpose != ApiConstant.API_ADD_MONEY_STEP2 ->
                            Utility.showToast(PockketApplication.instance.getString(R.string.something_went_wrong_error1))
                    }
                }
            }
        }

    }


    override fun offLine() {
        internetError.postValue(true)
    }

    override fun progress(isStart: Boolean, message: String) {
        progressDialog.postValue(isStart)
    }

}
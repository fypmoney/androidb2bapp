package com.fypmoney.view.register.viewModel

import android.app.Application
import android.text.TextUtils
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.InterestEntity
import com.fypmoney.model.KycActivateAccountResponse
import com.fypmoney.model.KycActivateAccountResponseDetails
import com.fypmoney.model.KycInitResponse
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.SharedPrefUtils.Companion.getString
import com.fypmoney.util.Utility
import com.fypmoney.view.register.model.SendKycDetails
import java.util.*
import kotlin.collections.ArrayList

/*
* This is used to handle account creation related functionality
* */
class KycdetailViewModel(application: Application) : BaseViewModel(application) {
    var noDataFoundVisibility = ObservableField(false)

    var postKycScreenCode = MutableLiveData<String>()
    var firstName = MutableLiveData<String>()
    var panNumber = MutableLiveData<String>()
    var lastName = MutableLiveData<String>()
    var address = MutableLiveData<String>()
    var kycType = MutableLiveData<String>()
    var address2 = MutableLiveData<String>()
    var pincode = MutableLiveData<String>()
    var dob = MutableLiveData<String>()
    var dobDate = MutableLiveData<String>()
    var gender = MutableLiveData<String>()
    var onActivateAccountSuccess = MutableLiveData<KycActivateAccountResponseDetails>()
    var onDobClicked = MutableLiveData(false)

    var selectedInterestList = ArrayList<InterestEntity>()

    // this method check is email is valid or not


    /*
    * This method is used to handle click of continue
    * */
    fun onContinueClicked() {
        when {
            TextUtils.isEmpty(panNumber.value) -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.pan_empty_error))
            }
            kycType.value == "PAN" && panNumber.value?.length != 10 -> {

                Utility.showToast(PockketApplication.instance.getString(R.string.pan_lenght_error))
            }
            kycType.value == "ADHAR" && panNumber.value?.length != 12 -> {

                Utility.showToast(PockketApplication.instance.getString(R.string.aadhaar_lenght_error))
            }
            TextUtils.isEmpty(firstName.value) -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.first_name_empty_error))
            }
            TextUtils.isEmpty(lastName.value) -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.last_name_empty_error))
            }
            TextUtils.isEmpty(dobDate.value) -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.dob_empty_error))
            }
            TextUtils.isEmpty(gender.value) -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.gender_empty_error))
            }
            TextUtils.isEmpty(address.value) -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.address_empty_error))
            }
            TextUtils.isEmpty(address2.value) -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.address_2_empty_error))
            }
            TextUtils.isEmpty(pincode.value) -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.pin_empty_error))
            }
            pincode.value?.length != 6 -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.pin_lenght_error))
            }

            TextUtils.isEmpty(kycType.value) -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.kyc_empty_error))
            }
            else -> {

                var sendkycDetails = SendKycDetails()
                sendkycDetails.firstName = firstName.value
                sendkycDetails.lastName = lastName.value
                sendkycDetails.documentIdentifier = panNumber.value
                sendkycDetails.address = address.value + ", " + address2.value
                sendkycDetails.dob = dobDate.value
                sendkycDetails.pincode = pincode.value
                if (gender.value == "Male") {
                    sendkycDetails.gender = "MALE"
                } else {
                    sendkycDetails.gender = "FEMALE"
                }
                sendkycDetails.kycDocumentType = kycType.value


                callKycAccountActivationApi(sendkycDetails)

            }
        }
    }

    private fun callKycAccountActivationApi(sendkycDetails: SendKycDetails) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_KYC_ACTIVATE_ACCOUNT,
                NetworkUtil.endURL(ApiConstant.API_KYC_ACTIVATE_ACCOUNT),
                ApiUrl.PUT,
                sendkycDetails,
                this, isProgressBar = true
            )
        )
    }

    /*
 * This method is used to handle click of date of birth
 * */
    fun onDobClicked() {
        onDobClicked.value = true
    }


    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_KYC_ACTIVATE_ACCOUNT -> {
                if (responseData is KycActivateAccountResponse) {
                    postKycScreenCode.value =
                        responseData.kycActivateAccountResponseDetails.postKycScreenCode
                    onActivateAccountSuccess.value = responseData.kycActivateAccountResponseDetails
                }
            }

        }
    }


}
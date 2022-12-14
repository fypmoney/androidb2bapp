package com.fypmoney.view.register.viewModel

import android.app.Application
import android.text.TextUtils
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.fyp.trackr.models.*
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.CustomerInfoResponse
import com.fypmoney.model.KycActivateAccountResponse
import com.fypmoney.model.KycActivateAccountResponseDetails
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.register.model.SendKycDetails
import com.moengage.core.internal.MoEConstants

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
    var customerProfileSuccess = MutableLiveData<Boolean>()
    var onDobClicked = MutableLiveData(false)
    var onAadharCardInfoClicked = MutableLiveData(false)


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
                Utility.showToast(PockketApplication.instance.getString(R.string.full_name_empty_error))
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
                sendkycDetails.lastName = ""
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

    fun onAadharCardInfoClicked(){
        onAadharCardInfoClicked.value = true
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
   *This method is used to call get customer profile API
   * */
    private fun callGetCustomerProfileApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_GET_CUSTOMER_INFO,
                endpoint = NetworkUtil.endURL(ApiConstant.API_GET_CUSTOMER_INFO),
                request_type = ApiUrl.GET,
                onResponse = this, isProgressBar = true,
                param = ""
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
                    trackr {
                        it.name = TrackrEvent.kyc_detail_fill_action
                        it.add(
                            TrackrField.status, "success")
                    }
                    postKycScreenCode.value =
                        responseData.kycActivateAccountResponseDetails.postKycScreenCode
                        onActivateAccountSuccess.value = responseData.kycActivateAccountResponseDetails
                        callGetCustomerProfileApi()
                }
            }
            ApiConstant.API_GET_CUSTOMER_INFO -> {
                if (responseData is CustomerInfoResponse) {
                    Utility.saveCustomerDataInPreference(responseData.customerInfoResponseDetails)
                    SharedPrefUtils.putString(
                        PockketApplication.instance,
                        SharedPrefUtils.SF_KYC_TYPE,responseData.customerInfoResponseDetails?.bankProfile?.kycType)
                    // Save the user id in shared preference
                    SharedPrefUtils.putLong(
                        getApplication(), key = SharedPrefUtils.SF_KEY_USER_ID,
                        value = responseData.customerInfoResponseDetails?.id!!
                    )
                    // Save the user phone in shared preference
                    SharedPrefUtils.putString(
                        getApplication(), key = SharedPrefUtils.SF_KEY_USER_MOBILE,
                        value = responseData.customerInfoResponseDetails?.mobile
                    )
                    SharedPrefUtils.putString(
                        getApplication(),
                        SharedPrefUtils.SF_KEY_PROFILE_IMAGE,
                        responseData.customerInfoResponseDetails?.profilePicResourceId
                    )
                    val interestList = ArrayList<String>()
                    if (responseData.customerInfoResponseDetails?.userInterests?.isNullOrEmpty() == false) {
                        responseData.customerInfoResponseDetails?.userInterests!!.forEach {
                            interestList.add(it.name!!)
                        }
                        SharedPrefUtils.putArrayList(
                            getApplication(),
                            SharedPrefUtils.SF_KEY_USER_INTEREST, interestList
                        )
                    }
                    if (postKycScreenCode.value.isNullOrEmpty()) {
                        responseData.customerInfoResponseDetails?.postKycScreenCode?.let {
                            postKycScreenCode.value = it
                        }


                    }

                    responseData.customerInfoResponseDetails?.postKycScreenCode?.let {
                        val map = hashMapOf<String, Any>()
                        map[CUSTOM_USER_POST_KYC_CODE] = it
                        UserTrackr.push(map)
                    }
                    responseData.customerInfoResponseDetails?.userProfile?.let { userProfile ->
                        val map1 = hashMapOf<String, Any>()
                        map1[MoEConstants.USER_ATTRIBUTE_USER_GENDER] =
                            userProfile.gender.toString()
                        UserTrackr.push(map1)
                        userProfile.dob?.let { it1 -> UserTrackr.setDateOfBirthDate(it1) }

                    }
                    customerProfileSuccess.value = true
//                    Utility.getCustomerDataFromPreference()?.postKycScreenCode="90"
//                    postKycScreenCode.value = "90"


                }


            }


        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when(purpose){
            ApiConstant.API_KYC_ACTIVATE_ACCOUNT->{
                trackr {
                    it.name = TrackrEvent.kyc_detail_fill_action
                    it.add(
                        TrackrField.status, errorResponseInfo.errorCode)
                }
            }
        }
    }


}
package com.fypmoney.connectivity

import android.util.Log
import com.fypmoney.connectivity.ApiConstant.API_AUTH_LOGIN
import com.fypmoney.connectivity.ApiConstant.API_CHECK_IS_APP_USER
import com.fypmoney.connectivity.ApiConstant.API_FETCH_ALL_FEEDS
import com.fypmoney.connectivity.ApiConstant.API_GET_CUSTOMER_INFO
import com.fypmoney.connectivity.ApiConstant.API_GET_INTEREST
import com.fypmoney.connectivity.ApiConstant.API_GET_NOTIFICATION_LIST
import com.fypmoney.connectivity.ApiConstant.API_LEAVE_FAMILY_MEMBER
import com.fypmoney.connectivity.ApiConstant.API_LOGIN
import com.fypmoney.connectivity.ApiConstant.API_LOGIN_INIT
import com.fypmoney.connectivity.ApiConstant.API_SNC_CONTACTS
import com.fypmoney.connectivity.ApiConstant.API_UPDATE_PROFILE
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.model.*
import com.google.gson.Gson
import okhttp3.ResponseBody


class ApiDataParsing {
    companion object {
        private var mApiDataParsing: ApiDataParsing? = null

        /**
         * @description Method to get single instance of WebApiCaller class.
         */
        @Synchronized
        fun getInstance(): ApiDataParsing {
            if (mApiDataParsing == null) {
                mApiDataParsing = ApiDataParsing()
            }
            return mApiDataParsing as ApiDataParsing
        }
    }

    /**
     * @param request ApiRequest;
     * @param body    ResponseBody;
     * @description Method is used to parse the data.
     */
    fun parseData(request: ApiRequest, body: ResponseBody): Any? {
        return try {
            val response = body.string()
            when (request.purpose) {
                API_AUTH_LOGIN -> {
                    getObject(response, AuthLoginResponse::class.java)
                }
                API_LOGIN_INIT -> {
                    getObject(response, LoginInitResponse::class.java)
                }
                API_LOGIN -> {
                    getObject(response, LoginResponse::class.java)
                }
                API_FETCH_ALL_FEEDS -> {
                    getObject(response, FeedResponseModel::class.java)
                }
                ApiConstant.API_ADD_FAMILY_MEMBER -> {
                    when (request.request_type) {
                        ApiUrl.GET -> {
                            getObject(response, GetMemberResponse::class.java)
                        }
                        else -> {
                            getObject(response, AddFamilyMemberResponse::class.java)
                        }
                    }

                }
                API_CHECK_IS_APP_USER -> {
                    getObject(response, IsAppUserResponse::class.java)
                }
                API_SNC_CONTACTS -> {
                    getObject(response, ContactResponse::class.java)
                }
                API_GET_CUSTOMER_INFO -> {
                    getObject(response, CustomerInfoResponse::class.java)
                }
                API_UPDATE_PROFILE -> {
                    getObject(response, CustomerInfoResponse::class.java)
                }

                API_GET_INTEREST -> {
                    getObject(response, InterestResponse::class.java)
                }
                API_LEAVE_FAMILY_MEMBER -> {
                    getObject(response, LeaveFamilyResponse::class.java)
                }
                ApiConstant.API_REMOVE_FAMILY_MEMBER -> {
                    getObject(response, RemoveFamilyResponse::class.java)
                }
                API_GET_NOTIFICATION_LIST -> {
                    getObject(response, NotificationModel.NotificationResponse::class.java)
                }
                ApiConstant.API_UPDATE_APPROVAL_REQUEST -> {
                    getObject(response, UpdateFamilyApprovalResponse::class.java)
                }
                ApiConstant.API_USER_TIMELINE -> {
                    getObject(response, NotificationModel.UserTimelineResponse::class.java)
                }
                ApiConstant.API_VERIFY_REFERRAL_CODE -> {
                    getObject(response, ReferralCodeResponse::class.java)
                }
                ApiConstant.API_UPDATE_FAMILY_NAME -> {
                    getObject(response, UpdateFamilyNameResponse::class.java)
                }

                ApiConstant.API_KYC_ACTIVATE_ACCOUNT -> {
                    getObject(response, KycActivateAccountResponse::class.java)
                }
                ApiConstant.API_KYC_INIT -> {
                    getObject(response, KycInitResponse::class.java)
                }
                ApiConstant.API_KYC_MOBILE_VERIFICATION -> {
                    getObject(response, KycMobileVerifyResponse::class.java)
                }
                ApiConstant.API_KYC_VERIFICATION -> {
                    getObject(response, KycVerificationResponse::class.java)
                }

                ApiConstant.API_GET_WALLET_BALANCE -> {
                    getObject(response, GetWalletBalanceResponse::class.java)
                }

                ApiConstant.API_GET_VIRTUAL_CARD_REQUEST -> {
                    getObject(response, VirtualCardRequestResponse::class.java)
                }

                ApiConstant.API_FETCH_VIRTUAL_CARD_DETAILS -> {
                    getObject(response, FetchVirtualCardResponse::class.java)
                }
                ApiConstant.API_FUND_TRANSFER -> {
                    getObject(response, SendMoneyResponse::class.java)
                }

                ApiConstant.API_REQUEST_MONEY -> {
                    getObject(response, RequestMoneyResponse::class.java)
                }
                ApiConstant.API_ADD_MONEY_STEP1 -> {
                    getObject(response, AddMoneyStep1Response::class.java)
                }

                ApiConstant.API_ADD_MONEY_STEP2 -> {
                    getObject(response, AddMoneyStep2Response::class.java)
                }
                ApiConstant.API_PAY_MONEY -> {
                    getObject(response, PayMoneyResponse::class.java)
                }
                ApiConstant.GET_USER_CARDS -> {
                    getObject(response, SavedCardResponseDetails::class.java)
                }
                ApiConstant.API_TRANSACTION_HISTORY -> {
                    getObject(response, TransactionHistoryResponse::class.java)
                }
                ApiConstant.API_BANK_TRANSACTION_HISTORY -> {
                    getObject(response, BankTransactionHistoryResponse::class.java)
                }

                ApiConstant.API_GET_HASH -> {
                    getObject(response, GetHashResponse::class.java)
                }
                ApiConstant.PAYU_PRODUCTION_URL -> {
                    getObject(response, CheckIsDomesticResponse::class.java)
                }
                else -> response
            }
        } catch (e: Exception) {
            request.onResponse.onError(request.purpose, NetworkUtil.responseData(e)!!)
            e.toString()
        }
    }

    /**
     * @description Method is used to convert json data into respective POJO class.
     */
    private fun <T> getObject(response: String, instance: Class<T>): Any? {
        return Gson().fromJson(response, instance)
    }
}
package com.fypmoney.connectivity.retrofit

import com.fypmoney.BuildConfig
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiDataParsing
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.model.*
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.view.giftcard.model.RequestGiftswithPage
import com.fypmoney.view.recharge.model.RechargeTypeModel
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.ResponseBody


/**
 * @description This class is responsible to hit the web API or to send response back to the server.
 */
class WebApiCaller {

    companion object {
        private var mWebApiCaller: WebApiCaller? = null

        /**
         * @description Method to get single instance of WebApiCaller class.
         */
        @Synchronized
        fun getInstance(): WebApiCaller {
            if (mWebApiCaller == null) {
                mWebApiCaller = WebApiCaller()
            }
            return mWebApiCaller as WebApiCaller
        }

    }

    /**
     * @param request ApiRequest
     * @description Method is used to hit singe web api at a time.
     */
    fun request(
        request: ApiRequest,
        whichServer: String? = null,
        command: String? = null,
        image: MultipartBody.Part? = null
    ) {

        var mObservable: Observable<ResponseBody>? = null

        val apiInterface: ApiInterface = when (whichServer) {
            AppConstants.PAYU_SERVER -> {
                ApiClient.getClient(ApiConstant.PAYU_BASE_URL).create(ApiInterface::class.java)

            }/*
            "https://run.mocky.io" -> {
                ApiClient.getClient("https://run.mocky.io").create(ApiInterface::class.java)

            }*/
            else -> {
                ApiClient.getClient(BuildConfig.BASE_ENDPOINT).create(ApiInterface::class.java)

            }
        }

       /* if (!isNetworkAvailable()) {
            Log.d(NetworkUtil.TAGN,"hasTransport wifi")
            //request.onResponse.offLine()
            return
        }*/

         when (request.request_type) {
            ApiUrl.GET -> {
                when (request.purpose) {
                    ApiConstant.API_CHECK_IS_APP_USER -> {
                        mObservable = apiInterface.getDataFromServer(
                            endPoint = request.endpoint,
                            authorization = SharedPrefUtils.getString(
                                PockketApplication.instance,
                                SharedPrefUtils.SF_KEY_ACCESS_TOKEN
                            ),
                            client_id = ApiConstant.CLIENT_ID
                        )
                    }
                    ApiConstant.API_USER_TIMELINE -> {

                        val params = request.param as Int


                        mObservable = apiInterface.getPaginationApiCalling2(
                            endPoint = request.endpoint,
                            authorization = SharedPrefUtils.getString(
                                PockketApplication.instance,
                                SharedPrefUtils.SF_KEY_ACCESS_TOKEN
                            ),
                            client_id = ApiConstant.CLIENT_ID,
                            page = params,
                            size = 6,
                            sort = null
                        )
                    }
                    ApiConstant.RewardsHistory -> {

                        val params = request.param as QueryPaginationParams


                        mObservable = apiInterface.getPaginationApiCalling2(
                            endPoint = request.endpoint,
                            authorization = SharedPrefUtils.getString(
                                PockketApplication.instance,
                                SharedPrefUtils.SF_KEY_ACCESS_TOKEN
                            ),
                            client_id = ApiConstant.CLIENT_ID,
                            page = params.page,
                            size = params.size,
                            sort = null
                        )
                    }
                    ApiConstant.API_GET_OPERATOR_LIST_MOBILE -> {

                        val params = request.param as RechargeTypeModel


                        mObservable = apiInterface.getRequestWithQueryType(
                            endPoint = request.endpoint,
                            authorization = SharedPrefUtils.getString(
                                PockketApplication.instance,
                                SharedPrefUtils.SF_KEY_ACCESS_TOKEN
                            ),
                            client_id = ApiConstant.CLIENT_ID,
                            type = params.type,

                            )
                    }
                    ApiConstant.CashbackHistory -> {

                        val params = request.param as QueryPaginationParams


                        mObservable = apiInterface.getPaginationApiCalling2(
                            endPoint = request.endpoint,
                            authorization = SharedPrefUtils.getString(
                                PockketApplication.instance,
                                SharedPrefUtils.SF_KEY_ACCESS_TOKEN
                            ),
                            client_id = ApiConstant.CLIENT_ID,
                            page = params.page,
                            size = params.size,
                            sort = null
                        )
                    }

                    ApiConstant.CHECK_APP_UPDATE -> {
                        mObservable = apiInterface.checkAppUpdate(
                            endPoint = request.endpoint
                        )
                    }
                    else -> {
                        mObservable = apiInterface.getDataFromServer1(
                            endPoint = request.endpoint,
                            authorization = SharedPrefUtils.getString(
                                PockketApplication.instance,
                                SharedPrefUtils.SF_KEY_ACCESS_TOKEN
                            ),
                            client_id = ApiConstant.CLIENT_ID, appId = ApiConstant.APP_ID
                        )
                    }
                }

            }
            ApiUrl.POST ->
                when (whichServer) {
                    AppConstants.PAYU_SERVER -> {
                        val params = request.param as PayUServerRequest
                        mObservable =
                            apiInterface.postDataOnPayUServer(
                                request.endpoint,
                                command = params.command!!,
                                key = params.key!!,
                                var1 = params.var1!!,
                                hash = params.hash
                            )
                    }

                    else -> {
                        when (request.purpose) {


                            ApiConstant.API_HISTORY_TASK -> {

                                val params = request.param as GetTaskResponse

                                mObservable = apiInterface.getPaginationApiCalling(
                                    endPoint = request.endpoint,
                                    authorization = SharedPrefUtils.getString(
                                        PockketApplication.instance,
                                        SharedPrefUtils.SF_KEY_ACCESS_TOKEN
                                    ),
                                    client_id = ApiConstant.CLIENT_ID,
                                    page = params.page!!,
                                    size = params.size!!,
                                    sort = params.sort!!,
                                    request = BaseRequest()
                                )
                            }
                            ApiConstant.API_YOUR_TASK -> {

                                val params = request.param as GetTaskResponse
                                val params1 = GetTaskResponseIsassign()
                                params1.isAssignTask = params.isAssignTask

                                mObservable = apiInterface.getPaginationApiCalling(
                                    endPoint = request.endpoint,
                                    authorization = SharedPrefUtils.getString(
                                        PockketApplication.instance,
                                        SharedPrefUtils.SF_KEY_ACCESS_TOKEN
                                    ),
                                    client_id = ApiConstant.CLIENT_ID,
                                    page = params.page!!,
                                    size = params.size!!,
                                    sort = params.sort!!,
                                    request = params1
                                )
                            }
                            ApiConstant.API_ASSIGN_TASK -> {

                                val params = request.param as GetTaskResponse
                                val params1 = GetTaskResponseIsassign()
                                params1.isAssignTask = params.isAssignTask

                                mObservable = apiInterface.getPaginationApiCalling(
                                    endPoint = request.endpoint,
                                    authorization = SharedPrefUtils.getString(
                                        PockketApplication.instance,
                                        SharedPrefUtils.SF_KEY_ACCESS_TOKEN
                                    ),
                                    client_id = ApiConstant.CLIENT_ID,
                                    page = params.page!!,
                                    size = params.size!!,
                                    sort = null,
                                    request = params1
                                )
                            }
                            ApiConstant.GET_GIFTS_LIST -> {

                                val params = request.param as RequestGiftswithPage
                                val params1 = params.request!!
                                params1.searchCriteria = ""

                                mObservable = apiInterface.getPaginationApiCalling(
                                    endPoint = request.endpoint,
                                    authorization = SharedPrefUtils.getString(
                                        PockketApplication.instance,
                                        SharedPrefUtils.SF_KEY_ACCESS_TOKEN
                                    ),
                                    client_id = ApiConstant.CLIENT_ID,
                                    page = params.page!!,
                                    size = params.size!!,
                                    sort = params.sort,
                                    request = params1
                                )
                            }

                            ApiConstant.API_GET_NOTIFICATION_LIST -> {


                                val params = request.param as NotificationModel.NotificationRequest

                                if (params.page != null) {
                                    mObservable = apiInterface.getPaginationApiCalling(
                                        endPoint = request.endpoint,
                                        authorization = SharedPrefUtils.getString(
                                            PockketApplication.instance,
                                            SharedPrefUtils.SF_KEY_ACCESS_TOKEN
                                        ),
                                        client_id = ApiConstant.CLIENT_ID,
                                        page = params.page,
                                        size = 6,
                                        sort = null,
                                        request = BaseRequest()
                                    )
                                } else {
                                    mObservable =
                                        apiInterface.postDataOnServer(
                                            client_id = ApiConstant.CLIENT_ID,
                                            appId = ApiConstant.APP_ID,
                                            authorization = SharedPrefUtils.getString(
                                                PockketApplication.instance,
                                                SharedPrefUtils.SF_KEY_ACCESS_TOKEN
                                            ),
                                            endPoint = request.endpoint,
                                            request = request.param
                                        )


                                }


                            }


                            ApiConstant.API_TRANSACTION_HISTORY -> {

                                val params1 = request.param as TransactionHistoryRequestwithPage
                                val params =
                                    TransactionHistoryRequest(destinationUserId = params1.destinationUserId)


                                mObservable = apiInterface.getPaginationApiCalling(
                                    endPoint = request.endpoint,
                                    authorization = SharedPrefUtils.getString(
                                        PockketApplication.instance,
                                        SharedPrefUtils.SF_KEY_ACCESS_TOKEN
                                    ),
                                    client_id = ApiConstant.CLIENT_ID,
                                    page = params1.page!!,
                                    size = 10,
                                    sort = "createdDate,desc",
                                    request = params


                                )
                            }
                            ApiConstant.API_BANK_TRANSACTION_HISTORY -> {

                                val params1 = request.param as BankTransactionHistoryRequestwithpage
                                val params =
                                    BankTransactionHistoryRequest(
                                        startDate = params1.startDate,
                                        endDate = params1.endDate
                                    )
                                var page = params1.page
                                if (page == null) {


                                    page = 0
                                }


                                mObservable = apiInterface.getPaginationApiCalling(
                                    endPoint = request.endpoint,
                                    authorization = SharedPrefUtils.getString(
                                        PockketApplication.instance,
                                        SharedPrefUtils.SF_KEY_ACCESS_TOKEN
                                    ),
                                    client_id = ApiConstant.CLIENT_ID,
                                    page = page,
                                    size = 10,
                                    sort = "createdDate,desc",
                                    request = params


                                )
                            }
                            ApiConstant.API_AUTH_LOGIN -> {
                                mObservable =
                                    apiInterface.postAuthDataOnServer(
                                        client_id = ApiConstant.CLIENT_ID,
                                        client_secret = ApiConstant.CLIENT_SECRET,
                                        grant_type = ApiConstant.GRANT_TYPE,
                                        endPoint = request.endpoint,
                                        request = request.param
                                    )
                            }
                            ApiConstant.API_LOGIN_INIT -> {
                                mObservable =
                                    apiInterface.postLoginInitDataOnServer(
                                        client_id = ApiConstant.CLIENT_ID,
                                        appId = ApiConstant.APP_ID,
                                        authorization = SharedPrefUtils.getString(
                                            PockketApplication.instance,
                                            SharedPrefUtils.SF_KEY_ACCESS_TOKEN
                                        ),
                                        one_tap = true,
                                        endPoint = request.endpoint,
                                        request = request.param
                                    )

                            }
                            ApiConstant.API_LOGIN -> {
                                mObservable =
                                    apiInterface.postLoginDataOnServer(
                                        client_id = ApiConstant.CLIENT_ID,
                                        appId = ApiConstant.APP_ID,
                                        one_tap = true,
                                        endPoint = request.endpoint,
                                        request = request.param
                                    )

                            }
                            ApiConstant.API_UPLOAD_PROFILE_PIC -> {
                                mObservable =
                                    apiInterface.postImageOnServer(
                                        client_id = ApiConstant.CLIENT_ID,
                                        appId = ApiConstant.APP_ID,
                                        authorization = SharedPrefUtils.getString(
                                            PockketApplication.instance,
                                            SharedPrefUtils.SF_KEY_ACCESS_TOKEN
                                        ),
                                        endPoint = request.endpoint,
                                        file = image
                                    )
                            }

                            else -> {
                                mObservable =
                                    apiInterface.postDataOnServer(
                                        client_id = ApiConstant.CLIENT_ID,
                                        appId = ApiConstant.APP_ID,
                                        authorization = SharedPrefUtils.getString(
                                            PockketApplication.instance,
                                            SharedPrefUtils.SF_KEY_ACCESS_TOKEN
                                        ),
                                        endPoint = request.endpoint,
                                        request = request.param
                                    )


                            }
                        }
                    }
                }
            ApiUrl.PUT ->
                when (request.purpose) {
                    ApiConstant.API_LEAVE_FAMILY_MEMBER -> {
                        mObservable =
                            apiInterface.putDataOnServer(
                                endPoint = request.endpoint,
                                client_id = ApiConstant.CLIENT_ID,
                                appId = ApiConstant.APP_ID,
                                authorization = SharedPrefUtils.getString(
                                    PockketApplication.instance,
                                    SharedPrefUtils.SF_KEY_ACCESS_TOKEN
                                )
                            )

                    }
                    else -> {
                        mObservable =
                            apiInterface.putDataOnServerWithBody(
                                endPoint = request.endpoint,
                                client_id = ApiConstant.CLIENT_ID,
                                appId = ApiConstant.APP_ID,
                                request = request.param,
                                authorization = SharedPrefUtils.getString(
                                    PockketApplication.instance,
                                    SharedPrefUtils.SF_KEY_ACCESS_TOKEN
                                )
                            )
                    }

                }


            ApiUrl.DELETE -> {
                mObservable =
                    apiInterface.deleteDataOnServer(
                        endPoint = request.endpoint,
                        client_id = ApiConstant.CLIENT_ID,
                        appId = ApiConstant.APP_ID,
                        authorization = SharedPrefUtils.getString(
                            PockketApplication.instance,
                            SharedPrefUtils.SF_KEY_ACCESS_TOKEN
                        )
                    )

            }
        }
        mObservable?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<ResponseBody> {
                override fun onSubscribe(d: Disposable) {
                    if (request.isProgressBar!!) {
                        request.onResponse.progress(
                            true,
                            PockketApplication.instance.getString(R.string.please_wait)
                        )
                    }
                }

                override fun onNext(responseBody: ResponseBody) {
                    request.onResponse.onSuccess(
                        purpose = request.purpose,
                        ApiDataParsing.getInstance().parseData(request, responseBody, command)!!
                    )

                    request.onResponse.progress(false, null.toString())

                }

                override fun onError(e: Throwable) {
                    if (e is java.net.ConnectException) {
                        request.onResponse.onError(
                            purpose = request.purpose,
                            ErrorResponseInfo(
                                errorCode = "101",
                                msg = ""
                            )
                        )
                        request.onResponse.progress(false, null.toString())
                        e.printStackTrace()
                    } else if (e is java.net.SocketTimeoutException) {

                        request.onResponse.onError(
                            purpose = request.purpose,
                            ErrorResponseInfo(
                                errorCode = "102",
                                msg = ""
                            )
                        )
                        request.onResponse.progress(false, null.toString())
                        e.printStackTrace()
                    } else {
                        try {
                            if (request.purpose == ApiConstant.API_LOGOUT) {
                                request.onResponse.onError(
                                    purpose = request.purpose,
                                    ErrorResponseInfo(
                                        errorCode = "204",
                                        msg = ""
                                    )
                                )


                            } else {
                                request.onResponse.onError(
                                    purpose = request.purpose,
                                    NetworkUtil.responseData(e)!!
                                )
                            }
                            if (request.isProgressBar!!) {
                                request.onResponse.progress(false, null.toString())
                            }
                        } catch (e: Exception) {
                            request.onResponse.onError(
                                purpose = request.purpose,
                                ErrorResponseInfo(
                                    errorCode = "1",
                                    msg = ""
                                )
                            )
                            request.onResponse.progress(false, null.toString())
                            e.printStackTrace()
                        }
                    }

                }


                override fun onComplete() {
                    if (request.isProgressBar!!) {
                        request.onResponse.progress(false, null.toString())
                    }
                }
            })
    }


    /**
     * This interface is responsible to back response of Web Api.
     */
    interface OnWebApiResponse {
        fun progress(isStart: Boolean, message: String)

        fun onSuccess(purpose: String, responseData: Any)

        fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo)

        fun offLine()
    }

}
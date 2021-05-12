package com.fypmoney.connectivity.retrofit

import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiDataParsing
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.network.NetworkUtil.Companion.isNetworkAvailable
import com.fypmoney.util.SharedPrefUtils
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import java.lang.Exception


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
    fun request(request: ApiRequest) {
        if (!isNetworkAvailable()) {
            request.onResponse.offLine()
            return
        }
        var mObservable: Observable<ResponseBody>? = null
        val apiInterface =
            ApiClient.getClient().create(ApiInterface::class.java)

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
                when (request.purpose) {
                    ApiConstant.API_AUTH_LOGIN -> {
                        mObservable =
                            apiInterface.postAuthDataOnServer(
                                client_id = ApiConstant.CLIENT_ID,
                                client_secret = ApiConstant.CLIENT_SECRET,
                                grant_type = ApiConstant.GRANT_TYPE,
                                request.endpoint,
                                request.param
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
                                request.endpoint,
                                request.param
                            )

                    }
                    ApiConstant.API_LOGIN -> {
                        mObservable =
                            apiInterface.postLoginDataOnServer(
                                client_id = ApiConstant.CLIENT_ID,
                                appId = ApiConstant.APP_ID,
                                one_tap = true,
                                request.endpoint,
                                request.param
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
                                request.endpoint,
                                request = request.param
                            )


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
                        ApiDataParsing.getInstance().parseData(request, responseBody)!!
                    )
                }

                override fun onError(e: Throwable) {
                    try {
                        request.onResponse.onError(
                            purpose = request.purpose,
                            NetworkUtil.responseData(e)!!
                        )
                        if (request.isProgressBar!!) {
                            request.onResponse.progress(false, null.toString())
                        }
                    } catch (e: Exception) {
                        request.onResponse.progress(false, null.toString())
                        e.printStackTrace()
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
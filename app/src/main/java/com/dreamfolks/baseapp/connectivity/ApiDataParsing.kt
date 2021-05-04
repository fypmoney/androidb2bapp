package com.dreamfolks.baseapp.connectivity

import android.util.Log
import com.dreamfolks.baseapp.connectivity.ApiConstant.API_AUTH_LOGIN
import com.dreamfolks.baseapp.connectivity.ApiConstant.API_CHECK_IS_APP_USER
import com.dreamfolks.baseapp.connectivity.ApiConstant.API_FETCH_ALL_FEEDS
import com.dreamfolks.baseapp.connectivity.ApiConstant.API_GET_CUSTOMER_INFO
import com.dreamfolks.baseapp.connectivity.ApiConstant.API_GET_INTEREST
import com.dreamfolks.baseapp.connectivity.ApiConstant.API_LEAVE_FAMILY_MEMBER
import com.dreamfolks.baseapp.connectivity.ApiConstant.API_LOGIN
import com.dreamfolks.baseapp.connectivity.ApiConstant.API_LOGIN_INIT
import com.dreamfolks.baseapp.connectivity.ApiConstant.API_SNC_CONTACTS
import com.dreamfolks.baseapp.connectivity.ApiConstant.API_UPDATE_PROFILE
import com.dreamfolks.baseapp.connectivity.network.NetworkUtil
import com.dreamfolks.baseapp.connectivity.retrofit.ApiRequest
import com.dreamfolks.baseapp.model.*
import com.dreamfolks.baseapp.util.AppConstants
import com.google.android.gms.common.api.Api
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


                else -> response
            }
        } catch (e: Exception) {
            request.onResponse.onError(request.purpose,NetworkUtil.responseData(e)!!)
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
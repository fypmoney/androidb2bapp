package com.fypmoney.connectivity.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.connectivity.ErrorResponseInfo
import com.google.gson.Gson
import retrofit2.HttpException


/**
 * @description This class is responsible for network utilities function.
 */
class NetworkUtil {


    companion object {
        /**
         * @return boolean
         * @description This method is used to check internet connection.
         */
        fun isNetworkAvailable(): Boolean {
            val connectivityManager =
                PockketApplication.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val nw = connectivityManager.activeNetwork ?: return false
                val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
                return when {
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    //for other device how are able to connect with Ethernet
                    //   actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    //for check internet over Bluetooth
                    //   actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                    else -> false
                }
            } else {
                return connectivityManager.activeNetworkInfo?.isConnected ?: false
            }
        }


        /**
         * Handle different types of error response data from API
         */
        fun responseData(throwable: Throwable): ErrorResponseInfo? {
            if (throwable is HttpException) {
                val s = throwable.response()!!.errorBody()!!.string()
                return when (throwable.code()) {
                    //handle code 400,403,500
                    400, 401, 402, 403, 404, 408, 500 -> {
                        getErrorData(s)
                    }
                    else -> ErrorResponseInfo(
                        msg = PockketApplication.instance.getString(R.string.something_went_wrong_error),
                        errorCode = throwable.code().toString()
                    )
                }
            }else{
                return ErrorResponseInfo(
                    msg = PockketApplication.instance.getString(R.string.something_went_wrong_error),
                    errorCode = throwable.toString()
                )
            }
        }

        fun endURL(endpoint: String): String {
            return endpoint
        }


        /**
         * Get error code from API
         */
        private fun getErrorData(response: String?): ErrorResponseInfo? {
            val replace = response?.replace("\n", "")
            return Gson().fromJson(replace, ErrorResponseInfo::class.java)
        }
    }

}
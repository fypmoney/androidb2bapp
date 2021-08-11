package com.fypmoney.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

/*
* This class is used for handling chores
* */
class ChoresHistoryViewModel(application: Application) : BaseViewModel(application) {

    var TaskHistory: MutableLiveData<ArrayList<HistoryModelResponse>> = MutableLiveData()


    init {

        callSampleTask()
    }

    /*
      * This method is used to refresh on swipe
      *  */
    fun onRefresh() {


        callSampleTask()
    }
    /*
      * This method is used to call get family notification API
      * */


    /*
      * This method is used to call user timeline API
      * */

    private fun callSampleTask() {


        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_HISTORY_TASK,
                endpoint = NetworkUtil.endURL(ApiConstant.API_HISTORY_TASK),
                request_type = ApiUrl.POST,
                GetTaskResponse(page = 0, size = 10, sort = "createdDate,desc"), onResponse = this,
                isProgressBar = false
            )

        )


    }

    fun callSampleTask(page: Int) {
        Log.d("chacksample", "called")

        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_HISTORY_TASK,
                endpoint = NetworkUtil.endURL(ApiConstant.API_HISTORY_TASK),
                request_type = ApiUrl.POST,
                GetTaskResponse(page = page, size = 10, sort = "createdDate,desc"),
                onResponse = this,
                isProgressBar = false
            )

        )


    }


    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)

        when (purpose) {

            ApiConstant.API_HISTORY_TASK -> {
                Log.d("chacksample", responseData.toString())
                val json = JsonParser().parse(responseData.toString()) as JsonObject

                val array = Gson().fromJson<Array<HistoryModelResponse>>(
                    json.get("data").toString(),
                    Array<HistoryModelResponse>::class.java
                )
                val arrayList = ArrayList(array.toMutableList())
                TaskHistory.postValue(arrayList)


            }


        }

    }


    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
//        isLoading.set(false)
        Log.d("chacksample", errorResponseInfo.msg)

    }


}
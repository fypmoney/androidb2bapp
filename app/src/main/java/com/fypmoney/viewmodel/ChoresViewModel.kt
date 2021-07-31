package com.fypmoney.viewmodel

import android.app.Application
import android.text.TextUtils
import android.util.Log
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
import com.fypmoney.database.entity.MemberEntity
import com.fypmoney.database.entity.TaskEntity
import com.fypmoney.model.AssignedTaskResponse
import com.fypmoney.model.BaseRequest
import com.fypmoney.model.GetTaskResponse
import com.fypmoney.model.SampleTaskModel
import com.fypmoney.util.Utility
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

/*
* This class is used for handling chores
* */
class ChoresViewModel(application: Application) : BaseViewModel(application) {
    var onAddMoneyClicked = MutableLiveData(false)
    var AllAssigned: MutableLiveData<ArrayList<AssignedTaskResponse>> = MutableLiveData()

    var AllCompletedTask: MutableLiveData<ArrayList<AssignedTaskResponse>> = MutableLiveData()

    fun onSelectClicked() {
        onAddMoneyClicked.value = true
    }

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
        Log.d("chacksample","called")

        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_ASSIGN_TASK,
                endpoint = NetworkUtil.endURL(ApiConstant.API_ASSIGN_TASK),
                request_type = ApiUrl.POST,
                GetTaskResponse(
                    1,
                    0,
                    10,
                    "createdDate,desc"
                ), onResponse = this,
                isProgressBar = false
            )

        )
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_COMPLETED_TASK,
                endpoint = NetworkUtil.endURL(ApiConstant.API_COMPLETED_TASK),
                request_type = ApiUrl.POST,
                GetTaskResponse(
                    0,
                    0,
                    10,
                    "createdDate,desc"
                ), onResponse = this,
                isProgressBar = false
            )

        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)

        when (purpose) {

            ApiConstant.API_ASSIGN_TASK -> {
                Log.d("chacksample",responseData.toString())
                val json = JsonParser().parse(responseData.toString()) as JsonObject

                val array =  Gson().fromJson<Array<AssignedTaskResponse>>(json.get("data").toString(), Array<AssignedTaskResponse>::class.java)
                val arrayList = ArrayList(array.toMutableList())
                AllAssigned.postValue(arrayList)


            }

            ApiConstant.API_COMPLETED_TASK -> {
                Log.d("chacksample2",responseData.toString())
                val json = JsonParser().parse(responseData.toString()) as JsonObject

                val array =  Gson().fromJson<Array<AssignedTaskResponse>>(json.get("data").toString(), Array<AssignedTaskResponse>::class.java)
                val arrayList = ArrayList(array.toMutableList())
                AllCompletedTask.postValue(arrayList)


            }


        }

    }

    /*
    * This method is used to call get approval request api
    * */


    /*
   * This method is used to call pay money api
   * */

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
//        isLoading.set(false)
        Log.d("chacksample",errorResponseInfo.msg)

    }


}
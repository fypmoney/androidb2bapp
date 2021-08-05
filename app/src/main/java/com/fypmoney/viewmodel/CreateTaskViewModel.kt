package com.fypmoney.viewmodel

import android.app.Application
import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.*
import com.fypmoney.view.adapter.CreateTaskAdapter
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser


class CreateTaskViewModel(application: Application) : BaseViewModel(application),
    CreateTaskAdapter.OnSampleTaskClickListener {
    var sampleTaskAdapter = CreateTaskAdapter(this, this)
    var sampleTaskList: MutableLiveData<ArrayList<SampleTaskModel.SampleTaskDetails>> =
        MutableLiveData()
    var loading = MutableLiveData(true)
    var noDataFoundVisibility = ObservableField(false)
    var isPreviousVisible = ObservableField(false)
    var isTimeLineNoDataVisible = ObservableField(false)
    var isGetNotificationsRecyclerVisible = ObservableField(true)
    var positionSelected = ObservableField<Int>()
    var onNotificationClicked = MutableLiveData<Boolean>()

    var taskSelectedResponse = SampleTaskModel.SampleTaskDetails()
    val isLoading = ObservableBoolean()

    init {

        callSampleTask()
    }


    fun onRefresh() {
        isLoading.set(true)

        callSampleTask()
    }


    private fun callSampleTask() {
        Log.d("chacksample","called")

        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_GET_TASKMASTER,
                endpoint = NetworkUtil.endURL(ApiConstant.API_GET_TASKMASTER),
                request_type = ApiUrl.GET,

                param = BaseRequest(), onResponse = this,
                isProgressBar = false
            )

        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        isLoading.set(false)
        loading.postValue(false)
      when (purpose) {

            ApiConstant.API_GET_TASKMASTER -> {
                Log.d("chacksample",responseData.toString())


                val json = JsonParser().parse(responseData.toString()) as JsonObject

                val array =  Gson().fromJson<Array<SampleTaskModel.SampleTaskDetails>>(json.get("data").toString(), Array<SampleTaskModel.SampleTaskDetails>::class.java)
                val arrayList = ArrayList(array.toMutableList())
                sampleTaskList.postValue(arrayList)

            }




        }

    }



    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        isLoading.set(false)
        loading.postValue(false)
        Log.d("chacksample", errorResponseInfo.msg)

    }



    override fun onNotificationClick(
        notification: SampleTaskModel.SampleTaskDetails?,
        position: Int
    ) {
        positionSelected.set(position)
        taskSelectedResponse = notification!!
        onNotificationClicked.value = true
    }

}
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
class ChoresViewModel(application: Application) : BaseViewModel(application) {
    var onAddMoneyClicked = MutableLiveData(false)
    var loading = MutableLiveData(false)
    var YourAssigned: MutableLiveData<ArrayList<AssignedTaskResponse>> = MutableLiveData()
    var error: MutableLiveData<String> = MutableLiveData()

    var AssignedByYouTask: MutableLiveData<ArrayList<AssignedTaskResponse>> = MutableLiveData()
    var bottomSheetStatus: MutableLiveData<UpdateTaskGetResponse> = MutableLiveData()
    var bottomtaskwithdrawn: MutableLiveData<UpdateTaskGetResponse> = MutableLiveData()

    var TaskDetailResponse: MutableLiveData<TaskDetailResponse> = MutableLiveData()

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

    fun callSampleTask() {
        Log.d("chacksample", "called")
        loading.postValue(true)
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_YOUR_TASK,
                endpoint = NetworkUtil.endURL(ApiConstant.API_YOUR_TASK),
                request_type = ApiUrl.POST,
                GetTaskResponseIsassign(1), onResponse = this,
                isProgressBar = false
            )

        )
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_ASSIGN_TASK,
                endpoint = NetworkUtil.endURL(ApiConstant.API_ASSIGN_TASK),
                request_type = ApiUrl.POST,
                GetTaskResponse(
                    0,
                    0,
                    20,
                    "createdDate,desc"
                ), onResponse = this,
                isProgressBar = false
            )

        )

    }

    fun callTaskDetail(id: String) {
        Log.d("chacksample", "called")

        loading.postValue(true)
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_TASK_DETAIL,
                endpoint = NetworkUtil.endURL(ApiConstant.API_TASK_DETAIL) + id,
                request_type = ApiUrl.POST,
                BaseRequest(), onResponse = this,
                isProgressBar = false
            )

        )


    }

    fun callTaskAccept(state: String, entityId: String?, s: String) {
        loading.postValue(true)

        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_TASK_UPDATE,
                endpoint = NetworkUtil.endURL(ApiConstant.API_TASK_UPDATE),
                request_type = ApiUrl.PUT,
                SendTaskResponse(
                    state = state, taskId = entityId,
                    emojis = "",
                    comments = s
                ), onResponse = this,
                isProgressBar = false
            )

        )
    }

    //    fun getupdatetask(state: String, entityId: Int?) {
//
//        var mDisposable = CompositeDisposable()
//    var update  =  SendTaskResponse(state = state
//            ,taskId = entityId,
//        emojis="\u1F600",
//        comments="ACCEPTED the task"
//    )
//
//        mDisposable!!.add(apiService.updateTask(update)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeWith(object : DisposableSingleObserver<JsonObject>() {
//                override fun onSuccess(response: JsonObject) {
//
//
//                 val mJsonString =response.get("data").toString()
//                    val parser = JsonParser()
//                    val mJson = parser.parse(mJsonString)
//                    Log.d("httpist", response.toString())
//                    if(mJson!=null){
//
//                        val task = Gson().fromJson(response.get("data"), UpdateTaskGetResponse::class.java)
//
//                        bottomSheetStatus.postValue(task)
//
//
//                                          }
//                }
//
//                override fun onError(e: Throwable) {
//                    Log.d("httpist",e.message.toString())
//                }
//            }))
//
//    }
    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        loading.postValue(false)
        when (purpose) {

            ApiConstant.API_YOUR_TASK -> {
                loading?.postValue(false)
                Log.d("chacksample", responseData.toString())
                val json = JsonParser().parse(responseData.toString()) as JsonObject

                val array = Gson().fromJson<Array<AssignedTaskResponse>>(
                    json.get("data").toString(),
                    Array<AssignedTaskResponse>::class.java
                )
                val arrayList = ArrayList(array.toMutableList())
                YourAssigned.postValue(arrayList)
                loading.postValue(false)


            }
            ApiConstant.API_TASK_DETAIL -> {
                Log.d("chacksample2", responseData.toString())
                val json = JsonParser().parse(responseData.toString()) as JsonObject

                val task = Gson().fromJson(
                    json.get("data"),
                    com.fypmoney.model.TaskDetailResponse::class.java
                )

                TaskDetailResponse.postValue(task)


            }
            ApiConstant.API_TASK_UPDATE -> {
                Log.d("chacksampleupdate", responseData.toString())


                val json = JsonParser().parse(responseData.toString()) as JsonObject
                val task = Gson().fromJson(json.get("data"), UpdateTaskGetResponse::class.java)

                bottomSheetStatus.postValue(task)


            }


            ApiConstant.API_ASSIGN_TASK -> {
                Log.d("chacksample2", responseData.toString())
                val json = JsonParser().parse(responseData.toString()) as JsonObject

                val array = Gson().fromJson<Array<AssignedTaskResponse>>(
                    json.get("data").toString(),
                    Array<AssignedTaskResponse>::class.java
                )
                val arrayList = ArrayList(array.toMutableList())
                AssignedByYouTask.postValue(arrayList)
                loading.postValue(false)

            }


        }

    }


    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        loading.postValue(false)
//        isLoading.set(false)
        Log.d("chacksample", errorResponseInfo.msg)
        error.postValue(errorResponseInfo.msg)
        loading?.postValue(false)
    }


}
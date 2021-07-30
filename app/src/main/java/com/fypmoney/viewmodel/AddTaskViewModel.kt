package com.fypmoney.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.databinding.ObservableArrayList
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
import com.fypmoney.model.*
import com.fypmoney.model.addTaskModal.AddTaskRequest
import com.fypmoney.view.adapter.RelationTaskAdapter
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

/*
* This class is used for handling chores
* */
class AddTaskViewModel(application: Application) : BaseViewModel(application) {
    var onAddMoneyClicked = MutableLiveData(false)

    var selectedRelationList = ObservableArrayList<RelationModel>()
    var onFromContactClicked = MutableLiveData<Boolean>()


    init {
        val list = PockketApplication.instance.resources.getStringArray(R.array.relationNameList)
        val iconList = PockketApplication.instance.resources.getIntArray(R.array.relationIconList)


    }
     fun callSampleTask(amount: String, title: String) {


        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_CREATE_TASK,
                NetworkUtil.endURL(ApiConstant.API_CREATE_TASK),
                ApiUrl.POST,
                AddTaskRequest(amount,"mm","2021-08-15T19:12:35Z",0,
                    "INR",28397,"2021-08-04T19:12:35Z",title)
                ,
                this, isProgressBar = true
            )
        )
    }
    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
//        isLoading.set(false)
        when (purpose) {

            ApiConstant.API_CREATE_TASK -> {
                Log.d("chacksample",responseData.toString())


                val json = JsonParser().parse(responseData.toString()) as JsonObject

                Toast.makeText(getApplication(),json.get("msg").toString(),Toast.LENGTH_SHORT).show()

                val array =  Gson().fromJson<GotAfterTaskResponse>(json.get("data").toString(), GotAfterTaskResponse::class.java)


            }




        }

    }



    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)

        Log.d("chacksample",errorResponseInfo.msg)

    }


    fun onSelectClicked() {
        onAddMoneyClicked.value = true
    }

}
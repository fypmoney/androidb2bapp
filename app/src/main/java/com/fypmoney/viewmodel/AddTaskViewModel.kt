package com.fypmoney.viewmodel

import android.app.Application
import android.util.Log
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
import com.fyp.trackr.services.TrackrServices
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.database.MemberRepository
import com.fypmoney.database.entity.MemberEntity
import com.fypmoney.model.*
import com.fypmoney.model.addTaskModal.AddTaskRequest
import com.fypmoney.util.AppConstants
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

/*
* This class is used for handling chores
* */
class AddTaskViewModel(application: Application) : BaseViewModel(application) {
    var onAddMoneyClicked = MutableLiveData(false)
    var familyMemberList: MutableLiveData<ArrayList<MemberEntity>> = MutableLiveData()

    var selectedRelationList = ObservableArrayList<RelationModel>()
    var onFromContactClicked = MutableLiveData<Boolean>()
    private var memberRepository = MemberRepository(mDB = appDatabase)
    var bottomSheetStatus: MutableLiveData<UpdateTaskGetResponse> = MutableLiveData()

    init {
        val list = PockketApplication.instance.resources.getStringArray(R.array.relationNameList)
        val iconList = PockketApplication.instance.resources.getIntArray(R.array.relationIconList)

        callGetMemberApi()
    }

    fun callGetMemberApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_ADD_FAMILY_MEMBER,
                endpoint = NetworkUtil.endURL(ApiConstant.API_ADD_FAMILY_MEMBER),
                request_type = ApiUrl.GET,
                param = "", onResponse = this,
                isProgressBar = false
            )
        )


    }

    fun callAddTask(
        amount: String,
        title: String,
        userId: String?,
        desc: String,
        startdate: String,
        enddate: String,
        trim: String
    ) {

//yogesh =28369
        // ranjeet =28448
       var amountf = amount.toLong() * 100
        var send = AddTaskRequest(
            amountf.toString(), desc, enddate, 0,
            "INR", userId!!, startdate, title, trim
        )

        trackr {
            it.services = arrayListOf(
                TrackrServices.FIREBASE,
                TrackrServices.MOENGAGE,
                TrackrServices.FB,TrackrServices.ADJUST)
            it.name = TrackrEvent.mission_given_success
        }
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_CREATE_TASK,
                NetworkUtil.endURL(ApiConstant.API_CREATE_TASK),
                ApiUrl.POST,
                send,
                this, isProgressBar = true
            )
        )
    }
    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
//        isLoading.set(false)
        when (purpose) {

            ApiConstant.API_CREATE_TASK -> {


                val json = JsonParser().parse(responseData.toString()) as JsonObject

                var array = Gson().fromJson<UpdateTaskGetResponse>(
                    json.get("data").toString(),
                    UpdateTaskGetResponse::class.java
                )
                array.msg = json.get("msg").toString()
                bottomSheetStatus.postValue(array)

            }

            ApiConstant.API_ADD_FAMILY_MEMBER -> {

                if (responseData is GetMemberResponse) {

                    val approveList: ArrayList<MemberEntity> = ArrayList()
                    val inviteList = mutableListOf<MemberEntity>()
                    memberRepository.deleteAllMembers()
                    if (!responseData.GetMemberResponseDetails.isNullOrEmpty()) {

                        memberRepository.insertAllMembers(responseData.GetMemberResponseDetails)
                        memberRepository.getAllMembersFromDatabase()?.forEach {
                            when (it.status) {
                                AppConstants.ADD_MEMBER_STATUS_APPROVED -> {
                                    approveList.add(it)
                                }
//                                AppConstants.ADD_MEMBER_STATUS_INVITED -> {
//                                    inviteList.add(it)
//                                }
                            }

                        }
                        familyMemberList.postValue(approveList)


                    } else {

                    }
                }
            }


        }

    }



    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)


    }


    fun onSelectClicked() {
        onAddMoneyClicked.value = true
    }

}
package com.fypmoney.viewmodel

import android.app.Application
import android.text.TextUtils
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.database.entity.MemberEntity
import com.fypmoney.database.entity.TaskEntity
import com.fypmoney.model.RelationModel
import com.fypmoney.view.adapter.RelationTaskAdapter

/*
* This class is used for handling chores
* */
class AddTaskViewModel(application: Application) : BaseViewModel(application) {
    var onAddMoneyClicked = MutableLiveData(false)
    var relationTaskAdapter = RelationTaskAdapter(this)
    var selectedRelationList = ObservableArrayList<RelationModel>()
    var onFromContactClicked = MutableLiveData<Boolean>()


    init {
        val list = PockketApplication.instance.resources.getStringArray(R.array.relationNameList)
        val iconList = PockketApplication.instance.resources.getIntArray(R.array.relationIconList)
        val relationModelList = ArrayList<RelationModel>()

        list.forEachIndexed { index, it ->
            val relationModel = RelationModel()
            relationModel.relationName = it
            relationModel.relationImage = iconList[index]
            relationModelList.add(relationModel)
        }

        relationTaskAdapter.setList(relationModelList)

    }

    /*
* This method is used to handle click of continue
* */
    /*
    * This will handle the click of select
    * */
    fun onSelectClicked() {
        onAddMoneyClicked.value = true
    }

}
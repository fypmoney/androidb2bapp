package com.dreamfolks.baseapp.viewhelper

import android.view.View
import androidx.databinding.ObservableField
import com.dreamfolks.baseapp.database.entity.MemberEntity
import com.dreamfolks.baseapp.model.MemberModel
import com.dreamfolks.baseapp.util.AppConstants
import com.dreamfolks.baseapp.view.adapter.MemberAdapterViewAll

/*
* This is used to display all the members in the list
* */
class MemberHorizontalViewHelper(
    var memberEntity: MemberEntity?,
    var onMemberItemClickListener: MemberAdapterViewAll.OnMemberItemClickListener
) {
    var isRemoveVisible = ObservableField(true)
    fun init() {
        when (memberEntity?.status) {
            AppConstants.ADD_MEMBER_STATUS_INVITED -> {
                isRemoveVisible.set(false)
            }
            else -> {
                isRemoveVisible.set(true)
            }
        }
    }

    /*
    * This is used to handle remove option
    * */
    fun onRemoveClicked(view: View) {
        onMemberItemClickListener.onMemberClick(view.id, memberEntity!!)
    }

}
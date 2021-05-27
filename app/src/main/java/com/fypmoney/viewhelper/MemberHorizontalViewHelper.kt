package com.fypmoney.viewhelper

import android.view.View
import androidx.databinding.ObservableField
import com.fypmoney.database.entity.MemberEntity
import com.fypmoney.util.AppConstants
import com.fypmoney.view.adapter.MemberAdapterViewAll

/*
* This is used to display all the members in the list
* */
class MemberHorizontalViewHelper(
    var memberEntity: MemberEntity?
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

    }

}
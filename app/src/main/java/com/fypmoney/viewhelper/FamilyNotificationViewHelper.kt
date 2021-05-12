package com.fypmoney.viewhelper

import androidx.databinding.ObservableField
import com.fypmoney.model.FamilyNotificationResponseDetails

/*
* This is used to display all the members in the list
* */
class FamilyNotificationViewHelper(
    var position: Int,
    var familyList: FamilyNotificationResponseDetails?
) {
    var accepted = ObservableField<String>()
    var decline = ObservableField<String>()
    var cancel = ObservableField<String>()
    var isAcceptVisible = ObservableField(false)

    fun init() {
        val actionAllowedList = familyList?.actionAllowed?.split(",")
        if (actionAllowedList?.size!! > 1) {
            isAcceptVisible.set(true)
            accepted.set(actionAllowedList[0])
            decline.set(actionAllowedList[1])
        } else {
            isAcceptVisible.set(false)
            cancel.set(familyList?.actionAllowed)
        }

    }


}
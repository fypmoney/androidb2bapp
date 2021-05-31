package com.fypmoney.viewhelper

import androidx.databinding.ObservableField
import com.fypmoney.model.NotificationModel

/*
* This is used to display all the timelines in the list
* */
class UserTimeLineViewHelper(
    var size:Int,
    var position: Int,
    var notification: NotificationModel.UserTimelineResponseDetails?
) {
    var accepted = ObservableField<String>()
    var decline = ObservableField<String>()
    var cancel = ObservableField<String>()
    var isAcceptVisible = ObservableField(false)
    var isDividerVisible = ObservableField(true)

    fun init() {
        if(position==size-1){
            isDividerVisible.set(false)
        }


        /* val actionAllowedList = notification?.actionAllowed?.split(",")
         if (actionAllowedList?.size!! > 1) {
             isAcceptVisible.set(true)
             accepted.set(actionAllowedList[0])
             decline.set(actionAllowedList[1])
         } else {
             isAcceptVisible.set(false)
             cancel.set(notification?.actionAllowed)
         }
 */
    }



}
package com.fypmoney.viewhelper

import androidx.databinding.ObservableField
import com.fypmoney.model.NotificationModel
import com.fypmoney.view.adapter.NotificationAdapter

/*
* This is used to display all the notifications in the list
* */
class NotificationViewHelper(
    var size: Int,
    var position: Int,
    var notification: NotificationModel.NotificationResponseDetails?,
    var onNotificationClickListener: NotificationAdapter.OnNotificationClickListener
) {
    var actionAllowed = ObservableField<String>()
    var isDividerVisible = ObservableField(true)

    fun init() {
        if (position == size - 1) {
            isDividerVisible.set(false)
        }

        val actionAllowedList = notification?.actionAllowed?.split(",")
        if (actionAllowedList?.size!! > 1) {
            actionAllowed.set(actionAllowedList.get(0))

        } else {
            actionAllowed.set(notification?.actionAllowed)
        }

    }

    fun onItemClicked() {
        onNotificationClickListener.onNotificationClick(notification, position)

    }


}
package com.fypmoney.viewhelper

import android.util.Log
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
    var accepted = ObservableField<String>()
    var decline = ObservableField<String>()
    var cancel = ObservableField<String>()
    var isDividerVisible = ObservableField(true)

    fun init() {
        if (position == size - 1) {
            isDividerVisible.set(false)
        }

    }

    fun onItemClicked() {
        onNotificationClickListener.onNotificationClick(notification, position)

    }


}
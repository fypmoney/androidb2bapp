package com.fypmoney.view.contacts.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

const val CONTACT_ACTIVITY_UI_MODEL = "contact_activity_ui_model"

sealed class ContactActivityActionEvent:Parcelable{
    @Parcelize object AddMember:ContactActivityActionEvent()
    @Parcelize object PayToContact:ContactActivityActionEvent()
}

@Parcelize
@Keep
data class ContactsActivityUiModel(
    var toolBarTitle:String,
    var showLoadingBalance:Boolean,
    var contactClickAction:ContactActivityActionEvent

):Parcelable

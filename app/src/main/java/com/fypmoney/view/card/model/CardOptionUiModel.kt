package com.fypmoney.view.card.model

import android.graphics.drawable.Drawable
import androidx.annotation.Keep

@Keep
data class CardOptionUiModel(
    var optionEvent:CardOptionEvent,
    var name:String,
    var icon:Drawable?
)
sealed class CardOptionEvent{
    object OrderCard:CardOptionEvent()
    object CardSettings:CardOptionEvent()
    object AccountStatement:CardOptionEvent()
    object TrackOrder:CardOptionEvent()
    object ActivateCard:CardOptionEvent()
}

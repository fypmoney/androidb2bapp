package com.fypmoney.view.home.main.home.model

import android.graphics.drawable.Drawable
import com.fypmoney.view.home.main.home.viewmodel.HomeFragmentVM

data class QuickActionUiModel(
    var id: HomeFragmentVM.QuickActionEvent,
    var image:Drawable?,
    var name:String
)

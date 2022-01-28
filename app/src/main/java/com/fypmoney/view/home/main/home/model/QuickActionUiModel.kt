package com.fypmoney.view.home.main.home.model

import android.graphics.drawable.Drawable
import androidx.annotation.Keep
import com.fypmoney.view.home.main.home.viewmodel.HomeFragmentVM
@Keep
data class QuickActionUiModel(
    var id: HomeFragmentVM,
    var image:Drawable?,
    var name:String
)

package com.fypmoney.view.register.model

import android.graphics.drawable.Drawable
import androidx.annotation.Keep

@Keep
data class SelectRelationModel(
    var isSelected: Int = 0,
    var relationImage: Drawable? = null,
    var relationName: String? = null,
    var backgroundColor: String? = null

)

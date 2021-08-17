package com.fypmoney.bindingAdapters

import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.fypmoney.R
import com.google.firebase.crashlytics.FirebaseCrashlytics


@BindingAdapter(value = ["app:imageUrl", "app:placeHolderDrawable", "app:rounded"], requireAll = false)
fun loadImage(view: ImageView, imageUrl: String?, placeHolderDrawable: Drawable?, rounded: Boolean?) {
    if (imageUrl != null) {
        val isRounded = rounded ?: false
        var glide = Glide.with(view.context)
            .load(imageUrl)
            .error(placeHolderDrawable)
            .placeholder(placeHolderDrawable)
        if (isRounded) {
            glide = glide.circleCrop()
        }
        glide.into(view)
    } else {
        view.setImageDrawable(placeHolderDrawable)
    }
}

@BindingAdapter("app:customBackgroundColor", "app:customCornerRadius",
    "app:customStrokeColor", "app:customStrokeWidth", "app:customIsRounded",
    "app:customBackgroundAlpha", requireAll = false
)
fun setBackgroundDrawable(
    view: View,
    @ColorInt backgroundColor: Int?,
    cornerRadius: Float?,
    @ColorInt strokeColor: Int?,
    strokeWidth: Float?,
    isRounded: Boolean?,
    alpha: Int?
) {
    try {
        val shapeDrawable = GradientDrawable()
        if (isRounded != null && isRounded) {
            shapeDrawable.shape = GradientDrawable.OVAL
        } else {
            shapeDrawable.shape = GradientDrawable.RECTANGLE
        }
        if (backgroundColor != null) {
            shapeDrawable.setColor(backgroundColor)
        }
        if (alpha != null) {
            shapeDrawable.alpha = 75
        }
        if (cornerRadius != null) {
            shapeDrawable.cornerRadius = cornerRadius
        }
        if (strokeColor != null && strokeWidth != null) {
            shapeDrawable.setStroke(strokeWidth.toInt(), strokeColor)
        }
        view.background = shapeDrawable
    } catch (e: Exception) {
        backgroundColor?.let {
            view.setBackgroundColor(it)
        }
    }

}

@BindingAdapter("app:customFirstName","app:customLastName",requireAll = false)
fun setFirstNameLastName(view: TextView, firstname: String?,lastname:String?) {
    if(lastname.isNullOrBlank()){
        view.text = firstname
    }else{
        view.text = String.format(view.context.resources.getString(R.string.first_name_last_name),firstname,lastname)
    }
}
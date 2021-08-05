package com.fypmoney.bindingAdapters

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide


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
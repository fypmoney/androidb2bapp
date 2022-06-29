package com.fypmoney.bindingAdapters

import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.animation.Animation
import android.view.animation.Interpolator
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.fypmoney.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily


@BindingAdapter(
    value = ["app:imageUrl", "app:placeHolderDrawable", "app:rounded"],
    requireAll = false
)
fun loadImage(
    view: ImageView,
    imageUrl: String?,
    placeHolderDrawable: Drawable?,
    rounded: Boolean?
) {
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

@BindingAdapter(
    "app:customBackgroundColor", "app:customCornerRadius",
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
    alpha: Int?)
{
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
fun setBackgroundDrawable(
    view: View,
    @ColorInt backgroundColor: Int?,
    cornerRadius: Float?,
    @ColorInt strokeColor: Int?,
    strokeWidth: Float?,
    isRounded: Boolean?)
{
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


fun setBackgroundDrawable(
    view: View,
    @ColorInt backgroundColor: Int?,
    cornerRadius: Float?,
    isRounded: Boolean?,
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

        if (cornerRadius != null) {
            shapeDrawable.cornerRadius = cornerRadius
        }

        view.background = shapeDrawable
    } catch (e: Exception) {
        backgroundColor?.let {
            view.setBackgroundColor(it)
        }
    }

}


@BindingAdapter("app:customFirstName", "app:customLastName", requireAll = false)
fun setFirstNameLastName(view: TextView, firstname: String?, lastname: String?) {
    if (lastname.isNullOrBlank()) {
        view.text = firstname
    } else {
        view.text = String.format(
            view.context.resources.getString(R.string.first_name_last_name),
            firstname,
            lastname
        )
    }
}

fun roundedImageView(
    imageView: ShapeableImageView,
    topRight: Float = 0.0f,
    topLeft: Float = 0.0f,
    bottomLeft: Float = 0.0f,
    bottomRight: Float = 0.0f
) {
    imageView.shapeAppearanceModel = imageView.shapeAppearanceModel
        .toBuilder()
        .setBottomRightCorner(CornerFamily.ROUNDED, bottomRight)
        .setBottomLeftCorner(CornerFamily.ROUNDED, bottomLeft)
        .setTopRightCorner(CornerFamily.ROUNDED, topRight)
        .setTopLeftCorner(CornerFamily.ROUNDED, topLeft)
        .build()
}

fun setSomePartOfTextInColor(
    textView: TextView,
    normalText: String,
    colorText: String,
    colorCode: String,
    remainingNormalText:String? = null
) {
    val spannableText = if(normalText == ""){
        "$colorText $remainingNormalText"
    }else if(remainingNormalText!=null){
        "$normalText $colorText $remainingNormalText"
    }
    else{
        "$normalText $colorText"
    }


    val spannable: Spannable = SpannableString(spannableText)

    spannable.setSpan(
        ForegroundColorSpan(Color.parseColor(colorCode)),
        normalText.length,
        (normalText + colorText).length + 1,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    textView.setText(spannable, TextView.BufferType.SPANNABLE)
}

fun doBounceAnimation(targetView: View) {
    val interpolator: Interpolator = Interpolator { v ->
        getPowOut(v, 2.0) //Add getPowOut(v,3); for more up animation
    }
    val animator = ObjectAnimator.ofFloat(targetView, "translationY", 0f, 25f, 0f)
    animator.interpolator = interpolator
    animator.startDelay = 100
    animator.duration = 800
    animator.repeatCount = -1
    animator.start()
}

private fun getPowOut(elapsedTimeRate: Float, pow: Double): Float {
    return (1.toFloat() - Math.pow((1 - elapsedTimeRate).toDouble(), pow)).toFloat()
}

fun shimmerDrawable(): ShimmerDrawable {
    val shimmer =
        Shimmer.AlphaHighlightBuilder()// The attributes for a ShimmerDrawable is set by this builder
            .setDuration(1800) // how long the shimmering animation takes to do one full sweep
            .setBaseAlpha(0.7f) //the alpha of the underlying children
            .setHighlightAlpha(0.6f) // the shimmer alpha amount
            .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
            .setAutoStart(true)
            .build()

    val shimmerDrawable = ShimmerDrawable().apply {
        setShimmer(shimmer)
    }
    return shimmerDrawable
}
fun shimmerColorDrawable(): ShimmerDrawable {
    val shimmer =
        Shimmer.ColorHighlightBuilder()// The attributes for a ShimmerDrawable is set by this builder
            .setDuration(1800) // how long the shimmering animation takes to do one full sweep
            .setBaseAlpha(0.7f) //the alpha of the underlying children
            .setHighlightAlpha(0.6f) // the shimmer alpha amount
            .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
            .setAutoStart(true)
            .setBaseColor(Color.parseColor("#e9e7e7"))
            .setHighlightColor(Color.parseColor("#e9e7e7"))
            .build()

    val shimmerDrawable = ShimmerDrawable().apply {
        setShimmer(shimmer)
    }
    return shimmerDrawable
}


/**
 * this method help to animation to view in x axis
 *
 */
fun setAnimationToViewInXAxis(
    v: View,
    startXAxisScale: Float,
    endXAxisScale: Float,
    duration: Long = 900
) {
    val anim: Animation = ScaleAnimation(
        startXAxisScale, endXAxisScale,  // Start and end values for the X axis scaling
        1f,// Start values for the Y axis scaling
        1f,  // End values for the Y axis scaling
        Animation.RELATIVE_TO_SELF, 0f,  // Pivot point of X scaling
        Animation.RELATIVE_TO_SELF, 1f
    ) // Pivot point of Y scaling
    anim.fillAfter = true // Needed to keep the result of the animation
    anim.duration = duration
    v.startAnimation(anim)
}

fun setAnimationToViewInYAxis(
    v: View,
    startYAxisScale: Float,
    endYAxisScale: Float,
    duration: Long = 900
) {
    val anim: Animation = ScaleAnimation(
        1f,// Start values for the X axis scaling
        1f,  // End values for the X axis scaling
        startYAxisScale,// Start values for the Y axis scaling
        endYAxisScale,  // End values for the Y axis scaling
        Animation.RELATIVE_TO_SELF, 0f,  // Pivot point of X scaling
        Animation.RELATIVE_TO_SELF, 1f
    ) // Pivot point of Y scaling
    anim.fillAfter = true // Needed to keep the result of the animation
    anim.duration = duration
    v.startAnimation(anim)
}

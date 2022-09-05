package com.fypmoney.extension

import android.app.Dialog
import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import com.fypmoney.R
import kotlin.math.roundToInt

/**
 * Make the view visible
 */
fun View.toVisible() {
    visibility = View.VISIBLE
}

/**
 * Make the view invisible
 */
fun View.toInvisible() {
    visibility = View.INVISIBLE
}

/**
 * Make the view gone
 */
fun View.toGone() {
    visibility = View.GONE
}

fun View.setMargin(left: Float, top: Float, right: Float, bottom: Float) {
    val leftPx =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            left,
            context.resources.displayMetrics
        ).toInt()
    val topPx = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        top,
        context.resources.displayMetrics
    ).toInt()
    val rightPx = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        right,
        context.resources.displayMetrics
    ).toInt()
    val bottomPx = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        bottom,
        context.resources.displayMetrics
    ).toInt()
    val layoutParams = layoutParams as ViewGroup.MarginLayoutParams
    layoutParams.setMargins(leftPx, topPx, rightPx, bottomPx)
}

fun View.bottomSheetTouchOutsideDisableOnly() {
    setOnClickListener(null)
}

val Number.toPx get() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    Resources.getSystem().displayMetrics)

val Float.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).roundToInt()

val Int.dp: Float
    get() = (this / Resources.getSystem().displayMetrics.density)
val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()
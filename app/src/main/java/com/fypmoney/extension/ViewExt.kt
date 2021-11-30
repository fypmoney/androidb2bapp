package com.fypmoney.extension

import android.util.TypedValue
import android.view.View
import android.view.ViewGroup

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

package com.fypmoney.util.textview

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View

open class MyStoreClickableSpan(
    var pos: Int,
    var clickableSpanListener: ClickableSpanListener
) : ClickableSpan() {

    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.isUnderlineText = true;
    }

    override fun onClick(widget: View) {
        clickableSpanListener.onPositionClicked(pos)

    }

}


interface ClickableSpanListener {
    fun onPositionClicked(pos: Int)
}
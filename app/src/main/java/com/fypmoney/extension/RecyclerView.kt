package com.fypmoney.extension

import androidx.databinding.ViewDataBinding


inline fun <T : ViewDataBinding> T.executeAfter(block: T.() -> Unit) {
    block()
    executePendingBindings()
}
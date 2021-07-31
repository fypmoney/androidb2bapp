package com.fypmoney.util

import android.text.method.PasswordTransformationMethod
import android.view.View


class AsteriskPasswordTransformationMethod : PasswordTransformationMethod() {
    override fun getTransformation(source: CharSequence?, view: View?): CharSequence {
        return PasswordCharSequence(source!!)
    }


    inner class PasswordCharSequence(val source: CharSequence) : CharSequence {
        override val length: Int
            get() = source.length

        override fun get(index: Int): Char =
            '*'

        override fun subSequence(start: Int, end: Int): CharSequence {
            return source.subSequence(start, end) // Return default
        }
    }
}
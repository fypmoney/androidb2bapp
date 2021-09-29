package com.fypmoney.util

import kotlin.math.ceil

fun roundOfAmountToCeli(amount:String): String {
    return ceil(amount.toFloat()).toInt().toString()
}
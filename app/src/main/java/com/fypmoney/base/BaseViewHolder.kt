package com.fypmoney.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 *Base view holder class
 */

abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun onBind(position: Int)
}
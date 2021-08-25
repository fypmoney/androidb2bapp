package com.fypmoney.view.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.base.BaseViewHolder
import com.fypmoney.databinding.ChoresStatusRowItemBinding

import com.fypmoney.model.ChoresTimeLineItem
import com.fypmoney.viewhelper.ChoresStatusViewHelper


class ChoresStatusAdapter(var statusList: List<ChoresTimeLineItem>) :
    RecyclerView.Adapter<BaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val mRowBinding = ChoresStatusRowItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(mRowBinding)


    }

    override fun getItemCount(): Int {
        return statusList?.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        return holder.onBind(position)
    }

    inner class ViewHolder(
        private val mRowItemBinding: ChoresStatusRowItemBinding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        lateinit var mViewHelper: ChoresStatusViewHelper

        override fun onBind(position: Int) {
            if (position <= statusList?.size!! - 2) {
                mViewHelper = ChoresStatusViewHelper(
                    position,
                    statusList?.get(position),
                    statusList?.size,
                    statusList?.get(position + 1)?.isDone
                )
            } else if (position == statusList?.size!! - 1) {
                mViewHelper = ChoresStatusViewHelper(
                    position,
                    statusList?.get(position),
                    statusList?.size,
                    statusList?.get(position)?.isDone
                )
            }
            mRowItemBinding!!.viewHelper = mViewHelper

            mRowItemBinding.executePendingBindings()

        }

    }

    /**
     * This will set the data in the list in adapter
     */


}
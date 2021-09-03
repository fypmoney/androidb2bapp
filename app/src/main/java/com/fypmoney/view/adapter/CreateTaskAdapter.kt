package com.fypmoney.view.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.base.BaseViewHolder
import com.fypmoney.databinding.CardSampleItemBinding


import com.fypmoney.model.NotificationModel
import com.fypmoney.model.SampleTaskModel
import com.fypmoney.viewhelper.CreateTaskViewHelper

import com.fypmoney.viewmodel.CreateTaskViewModel



/**
 * This adapter class is used to handle notifications
 */
class CreateTaskAdapter(var viewModel: CreateTaskViewModel, var onSampleTaskClickListener:OnSampleTaskClickListener) :
    RecyclerView.Adapter<BaseViewHolder>() {
    var taskList: ArrayList<SampleTaskModel.SampleTaskDetails>? = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val mRowBinding = CardSampleItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(mRowBinding)


    }

    override fun getItemCount(): Int {
        return taskList!!.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        return holder.onBind(position)
    }

    inner class ViewHolder(
        private val mRowItemBinding: CardSampleItemBinding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        lateinit var mViewHelper: CreateTaskViewHelper

        override fun onBind(position: Int) {
            mViewHelper = CreateTaskViewHelper(
                taskList?.size!!,
                position,
                taskList?.get(position),onSampleTaskClickListener
            )
            mRowItemBinding!!.viewHelper = mViewHelper
           // mRowItemBinding.viewModel = viewModel
            mViewHelper.init()
            mRowItemBinding.executePendingBindings()

        }

    }

    /**
     * This will set the data in the list in adapter
     */
    fun setList(notificationList1: List<SampleTaskModel.SampleTaskDetails>?) {
        try {
            taskList!!.clear()
            notificationList1?.forEach {
                taskList!!.add(it)
            }
            notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    interface OnSampleTaskClickListener
    {
        fun onNotificationClick(notification:SampleTaskModel.SampleTaskDetails?,position: Int)
    }

}
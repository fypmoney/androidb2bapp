package com.fypmoney.view.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.R
import com.fypmoney.base.BaseViewHolder
import com.fypmoney.databinding.NotificationRowItemBinding
import com.fypmoney.model.NotificationModel
import com.fypmoney.util.Utility
import com.fypmoney.viewhelper.NotificationViewHelper
import com.fypmoney.viewmodel.NotificationViewModel


/**
 * This adapter class is used to handle notifications
 */
class NotificationAdapter(var onNotificationClickListener: OnNotificationClickListener) :
    RecyclerView.Adapter<BaseViewHolder>() {
    var notificationList: ArrayList<NotificationModel.NotificationResponseDetails>? = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val mRowBinding = NotificationRowItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(mRowBinding)


    }

    override fun getItemCount(): Int {
        return notificationList!!.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        return holder.onBind(position)
    }

    inner class ViewHolder(
        private val mRowItemBinding: NotificationRowItemBinding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        lateinit var mViewHelper: NotificationViewHelper

        override fun onBind(position: Int) {
            mViewHelper = NotificationViewHelper(
                notificationList?.size!!,
                position,
                notificationList?.get(position), onNotificationClickListener
            )
            mRowItemBinding!!.viewHelper = mViewHelper
            if (!notificationList?.get(position)?.additionalAttributes?.amount.isNullOrEmpty()) {
                mRowItemBinding.amount.text =
                    """${mRowItemBinding.amount.context.resources.getString(R.string.Rs)}${
                        Utility.convertToRs(notificationList?.get(position)?.additionalAttributes?.amount)
                    }"""
            }else{
                mRowItemBinding.amount.text =""
            }

            // mRowItemBinding.viewModel = viewModel
            mViewHelper.init()
            mRowItemBinding.executePendingBindings()

        }

    }

    /**
     * This will set the data in the list in adapter
     */
    fun setList(notificationList1: List<NotificationModel.NotificationResponseDetails>?) {
        try {
            if (notificationList1 == null) {
                notificationList!!.clear()
            } else {
                notificationList!!.clear()
                notificationList1?.forEach {
                    notificationList!!.add(it)
                }
            }

            notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * This will set the data in the list in adapter
     */
    fun updateList(notification: NotificationModel.NotificationResponseDetails?, position: Int) {
        try {
            notificationList?.remove(notification)
            notifyItemRemoved(position)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    interface OnNotificationClickListener
    {
        fun onNotificationClick(notification:NotificationModel.NotificationResponseDetails?,position: Int)
    }

}
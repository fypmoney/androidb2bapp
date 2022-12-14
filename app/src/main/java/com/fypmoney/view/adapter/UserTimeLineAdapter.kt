package com.fypmoney.view.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.base.BaseViewHolder
import com.fypmoney.databinding.UserTimelineRowItemBinding
import com.fypmoney.model.NotificationModel
import com.fypmoney.viewhelper.UserTimeLineViewHelper
import com.fypmoney.viewmodel.NotificationViewModel


/**
 * This adapter class is used to handle user timelines
 */
class UserTimeLineAdapter() :
    RecyclerView.Adapter<BaseViewHolder>() {
    var notificationList: ArrayList<NotificationModel.UserTimelineResponseDetails>? = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val mRowBinding = UserTimelineRowItemBinding.inflate(
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
        private val mRowItemBinding: UserTimelineRowItemBinding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        lateinit var mViewHelper: UserTimeLineViewHelper

        override fun onBind(position: Int) {
            mViewHelper = UserTimeLineViewHelper(
                notificationList?.size!!,
                position,
                notificationList?.get(position)
            )
            mRowItemBinding!!.viewHelper = mViewHelper
            mViewHelper.init()
            mRowItemBinding.executePendingBindings()

        }

    }

    /**
     * This will set the data in the list in adapter
     */
    fun setList(notificationList1: List<NotificationModel.UserTimelineResponseDetails>?) {
        try {
            if (notificationList1 == null) {
                notificationList!!.clear()
            } else {

                notificationList1?.forEach {
                    notificationList!!.add(it)
                }
            }
            notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}
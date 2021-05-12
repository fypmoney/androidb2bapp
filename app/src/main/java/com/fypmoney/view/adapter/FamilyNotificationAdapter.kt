package com.fypmoney.view.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.base.BaseViewHolder
import com.fypmoney.databinding.FamilyNotificationRowItemBinding
import com.fypmoney.model.FamilyNotificationResponse
import com.fypmoney.model.FamilyNotificationResponseDetails
import com.fypmoney.viewhelper.FamilyNotificationViewHelper
import com.fypmoney.viewmodel.FamilyNotificationViewModel


/**
 * This adapter class is used to handle contacts
 */
class FamilyNotificationAdapter(var viewModel: FamilyNotificationViewModel) :
    RecyclerView.Adapter<BaseViewHolder>() {
    var notificationList: ArrayList<FamilyNotificationResponseDetails>? = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val mRowBinding = FamilyNotificationRowItemBinding.inflate(
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
        private val mRowItemBinding: FamilyNotificationRowItemBinding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        lateinit var mViewHelper: FamilyNotificationViewHelper

        override fun onBind(position: Int) {
            mViewHelper = FamilyNotificationViewHelper(
                position,
                notificationList?.get(position)
            )
            mRowItemBinding!!.viewHelper = mViewHelper
            mRowItemBinding.viewModel = viewModel
            mViewHelper.init()
            mRowItemBinding.executePendingBindings()

        }

    }

    /**
     * This will set the data in the list in adapter
     */
    fun setList(notificationList1: List<FamilyNotificationResponseDetails>?) {
        try {
            notificationList!!.clear()
            notificationList1?.forEach {
                notificationList!!.add(it)
            }
            notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * This will set the data in the list in adapter
     */
    fun updateList(notification: FamilyNotificationResponseDetails?, position: Int) {
        try {
            notificationList?.remove(notification)
            notifyItemRemoved(position)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
package com.fypmoney.view.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.base.BaseViewHolder
import com.fypmoney.databinding.OrderStatusRowItemBinding
import com.fypmoney.model.GetOrderCardStatusResponseDetails
import com.fypmoney.model.PackageStatusList
import com.fypmoney.viewhelper.OrderStatusViewHelper
import com.fypmoney.viewmodel.TrackOrderViewModel

/**
 * This adapter class is used to handle order status
 */
class OrderStatusAdapter(var viewModel: TrackOrderViewModel) :
    RecyclerView.Adapter<BaseViewHolder>() {
    var statusList: ArrayList<PackageStatusList>? = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val mRowBinding = OrderStatusRowItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(mRowBinding)


    }

    override fun getItemCount(): Int {
        return statusList!!.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        return holder.onBind(position)
    }

    inner class ViewHolder(
        private val mRowItemBinding: OrderStatusRowItemBinding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        lateinit var mViewHelper: OrderStatusViewHelper

        override fun onBind(position: Int) {
            mViewHelper = OrderStatusViewHelper(statusList?.get(position)
            )
            mRowItemBinding!!.viewHelper = mViewHelper
            mRowItemBinding.executePendingBindings()

        }

    }

    /**
     * This will set the data in the list in adapter
     */
    fun setList(statusList1: List<PackageStatusList>?) {
        try {
            statusList!!.clear()
            statusList1?.forEach {
                statusList!!.add(it)
            }
            notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
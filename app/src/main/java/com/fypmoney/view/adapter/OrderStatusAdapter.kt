package com.fypmoney.view.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.base.BaseViewHolder
import com.fypmoney.databinding.OrderStatusRowItemBinding
import com.fypmoney.model.PackageStatusList
import com.fypmoney.util.AppConstants
import com.fypmoney.viewhelper.OrderStatusViewHelper
import com.fypmoney.view.ordercard.trackorder.TrackOrderViewModel
import android.graphics.Paint




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
            if (position <= statusList?.size!! - 2) {
                mViewHelper = OrderStatusViewHelper(
                    position,
                    statusList?.get(position),
                    statusList?.size,
                    statusList?.get(position + 1)?.isDone
                )
            } else if (position == statusList?.size!! - 1) {
                mViewHelper = OrderStatusViewHelper(
                    position,
                    statusList?.get(position),
                    statusList?.size,
                    statusList?.get(position)?.isDone
                )
            }


            if(((statusList?.get(position)?.status)=="SHIPPED")
                and ((statusList?.get(position)?.isDone)==AppConstants.YES)){
                mRowItemBinding?.viewShippingDetailsTv?.paintFlags = mRowItemBinding?.viewShippingDetailsTv?.paintFlags!! or Paint.UNDERLINE_TEXT_FLAG
                mRowItemBinding.viewShippingDetailsTv.visibility = View.VISIBLE

            }else{
                mRowItemBinding?.viewShippingDetailsTv?.visibility = View.GONE

            }
            mRowItemBinding?.viewShippingDetailsTv?.setOnClickListener {
                viewModel.onShippingDetailsClick((statusList?.get(position)))
            }
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
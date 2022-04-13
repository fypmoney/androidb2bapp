package com.fypmoney.view.recharge.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.base.BaseViewHolder
import com.fypmoney.databinding.RechargeItemLayoutBinding
import com.fypmoney.databinding.RechargePostpaidLayoutBinding
import com.fypmoney.model.StoreDataModel


class RechargePostpaidAdapter(var onStoreClickListener: OnRechargeItemClick) :
    RecyclerView.Adapter<BaseViewHolder>() {
    var storeList: ArrayList<StoreDataModel>? = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {


        val mRowBinding = RechargePostpaidLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return AddUpiViewHolder(mRowBinding)

    }

    override fun getItemCount(): Int {
        return storeList!!.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        return holder.onBind(position)
    }


    inner class AddUpiViewHolder(
        private val mRowItemBinding: RechargePostpaidLayoutBinding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        private lateinit var mViewHelper: RechargepostpaidViewHelper
        override fun onBind(position: Int) {

            mViewHelper = RechargepostpaidViewHelper(
                position,
                storeList?.get(position), onStoreClickListener
            )

            mRowItemBinding?.tvServiceName?.text = storeList?.get(position)?.title
            storeList?.get(position)?.Icon?.let {
                mRowItemBinding?.ivServiceLogo?.setImageResource(
                    it
                )
            }

            mRowItemBinding?.viewHelper = mViewHelper
            mRowItemBinding?.executePendingBindings()
        }

    }

    interface OnRechargeItemClick {
        fun onRechargePostpaid(position: Int, upiModel: StoreDataModel?)
    }
//    interface onItemClicked {
//        fun  onRechargePostpaid(position:Int, upiModel :StoreDataModel)
//    }
    /**
     * This will set the data in the list in adapter
     */
    fun setList(addMoneyList1: List<StoreDataModel>?) {
        storeList!!.clear()

        addMoneyList1!!.forEach {
            storeList!!.add(it)
        }
        notifyDataSetChanged()
    }


}
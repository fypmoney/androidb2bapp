package com.fypmoney.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.base.BaseViewHolder
import com.fypmoney.databinding.*
import com.fypmoney.model.StoreDataModel
import com.fypmoney.viewhelper.StoreViewHelper


class StoreItemAdapter(var onStoreClickListener: OnStoreItemClick) :
    RecyclerView.Adapter<BaseViewHolder>() {
    var storeList: ArrayList<StoreDataModel>? = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {


                val mRowBinding = StoreItemLayoutBinding.inflate(
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
        private val mRowItemBinding: StoreItemLayoutBinding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        private lateinit var mViewHelper: StoreViewHelper
        override fun onBind(position: Int) {
            mViewHelper = StoreViewHelper(
                position,
                storeList?.get(position), onStoreClickListener
            )
    mRowItemBinding?.tvServiceName?.text= storeList?.get(position)?.title

            mRowItemBinding!!.viewHelper = mViewHelper
            mRowItemBinding.executePendingBindings()
        }

    }


    /**
     * This will set the data in the list in adapter
     */
    fun setList(addMoneyList1: List<StoreDataModel>?) {
        storeList!!.clear()
        val upiModel = StoreDataModel()

        storeList?.add(upiModel)

        addMoneyList1!!.forEach {
            storeList!!.add(it)
        }
        notifyDataSetChanged()
    }

    interface OnStoreItemClick {
        fun onStoreItemClicked(position: Int, upiModel: StoreDataModel?)
    }

}
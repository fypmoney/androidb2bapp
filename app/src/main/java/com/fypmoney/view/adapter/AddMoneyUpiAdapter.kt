package com.fypmoney.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.base.BaseViewHolder
import com.fypmoney.databinding.AddMonyUpiRowItemBinding
import com.fypmoney.databinding.AddUpiLayoutBinding
import com.fypmoney.model.UpiModel
import com.fypmoney.viewhelper.AddMoneyUpiViewHelper


/**
 * This adapter class is used to handle upi list in add money
 */
class AddMoneyUpiAdapter(var onUpiClickListener: OnUpiClickListener) :
    RecyclerView.Adapter<BaseViewHolder>() {
    var upiList: ArrayList<UpiModel>? = ArrayList()
    private val typeAdd = 1
    private val typeList = 2
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        when (viewType) {
            typeList -> {
                val mRowBinding = AddMonyUpiRowItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
                return ViewHolder(mRowBinding)
            }
            else -> {

                val mRowBinding = AddUpiLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
                return AddUpiViewHolder(mRowBinding)
            }


        }
    }

    override fun getItemCount(): Int {
        return upiList!!.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        return holder.onBind(position)
    }

    inner class ViewHolder(
        private val mRowItemBinding: AddMonyUpiRowItemBinding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        private lateinit var mViewHelper: AddMoneyUpiViewHelper
        override fun onBind(position: Int) {
            mViewHelper = AddMoneyUpiViewHelper(
                position,
                upiList?.get(position), onUpiClickListener
            )
            mRowItemBinding!!.viewHelper = mViewHelper
            mRowItemBinding.executePendingBindings()
        }

    }

    inner class AddUpiViewHolder(
        private val mRowItemBinding: AddUpiLayoutBinding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        private lateinit var mViewHelper: AddMoneyUpiViewHelper
        override fun onBind(position: Int) {
            mViewHelper = AddMoneyUpiViewHelper(
                position,
                upiList?.get(position), onUpiClickListener
            )
            mRowItemBinding!!.viewHelper = mViewHelper
            mRowItemBinding.executePendingBindings()
        }

    }

    override fun getItemViewType(position: Int): Int {
        return typeList/*when (position) {
            0 -> {
                typeAdd
            }
            else -> {
                typeList
            }
        }*/

    }

    /**
     * This will set the data in the list in adapter
     */
    fun setList(addMoneyList1: List<UpiModel>?) {
        upiList!!.clear()/*
        val upiModel = UpiModel()
        upiModel.name = PockketApplication.instance.getString(R.string.add_upi_text)
        upiList?.add(upiModel)*/
        addMoneyList1!!.forEach {
            upiList!!.add(it)
        }
        notifyDataSetChanged()
    }

    interface OnUpiClickListener {
        fun onUpiItemClicked(position: Int, upiModel: UpiModel?)
    }

}
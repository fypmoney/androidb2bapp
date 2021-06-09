package com.fypmoney.view.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.base.BaseViewHolder
import com.fypmoney.databinding.AddMonyUpiRowItemBinding
import com.fypmoney.model.UpiModel
import com.fypmoney.viewhelper.AddMoneyUpiViewHelper


/**
 * This adapter class is used to handle upi list in add money
 */
class AddMoneyUpiAdapter:
    RecyclerView.Adapter<BaseViewHolder>() {
    var upiList: ArrayList<UpiModel>? = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val mRowBinding = AddMonyUpiRowItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(mRowBinding)


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
                upiList?.get(position)
            )
            mRowItemBinding!!.viewHelper = mViewHelper
            mRowItemBinding.executePendingBindings()
        }

    }

    /**
     * This will set the data in the list in adapter
     */
    fun setList(addMoneyList1: List<UpiModel>?) {
        upiList!!.clear()
        addMoneyList1!!.forEach {
            upiList!!.add(it)
        }
        notifyDataSetChanged()
    }


}
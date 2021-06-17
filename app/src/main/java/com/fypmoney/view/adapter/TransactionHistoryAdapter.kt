package com.fypmoney.view.adapter


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.base.BaseViewHolder
import com.fypmoney.databinding.TransactionHistoryRowItemBinding
import com.fypmoney.model.TransactionHistoryResponseDetails
import com.fypmoney.viewhelper.TransactionHistoryViewHelper
import com.fypmoney.viewmodel.TransactionHistoryViewModel


/**
 * This adapter class is used to handle transaction history
 */
class TransactionHistoryAdapter(var viewModel: TransactionHistoryViewModel) :
    RecyclerView.Adapter<BaseViewHolder>() {
    var transactionList: ArrayList<TransactionHistoryResponseDetails>? = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val mRowBinding = TransactionHistoryRowItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(mRowBinding)


    }

    override fun getItemCount(): Int {
        return transactionList!!.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {

        return holder.onBind(position)
    }

    inner class ViewHolder(
        private val mRowItemBinding: TransactionHistoryRowItemBinding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        lateinit var mViewHelper: TransactionHistoryViewHelper


        override fun onBind(position: Int) {
            mViewHelper = TransactionHistoryViewHelper(
                position,
                transactionList?.get(position), viewModel
            )
            mRowItemBinding!!.viewHelper = mViewHelper
            mViewHelper.init()
            mRowItemBinding.executePendingBindings()

        }

    }

    /**
     * This will set the data in the list in adapter
     */
    fun setList(transactionList1: List<TransactionHistoryResponseDetails>?) {
        Log.d("jfgrburo",transactionList1?.size.toString())
        try {
            transactionList!!.clear()
            transactionList1?.forEach {
                transactionList!!.add(it)
            }
            notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



}
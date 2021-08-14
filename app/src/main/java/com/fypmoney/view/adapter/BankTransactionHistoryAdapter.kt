package com.fypmoney.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.base.BaseViewHolder
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.databinding.BankTransactionHistoryRowItemBinding
import com.fypmoney.model.BankTransactionHistoryResponseDetails
import com.fypmoney.viewhelper.BankTransactionHistoryViewHelper
import com.fypmoney.viewmodel.BankTransactionHistoryViewModel


/**
 * This adapter class is used to handle bank transaction history
 */
class BankTransactionHistoryAdapter(var viewModel: BankTransactionHistoryViewModel) :
    RecyclerView.Adapter<BaseViewHolder>() {
    var transactionList: ArrayList<BankTransactionHistoryResponseDetails>? = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val mRowBinding = BankTransactionHistoryRowItemBinding.inflate(
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
        private val mRowItemBinding: BankTransactionHistoryRowItemBinding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        lateinit var mViewHelper: BankTransactionHistoryViewHelper

        override fun onBind(position: Int) {
            mViewHelper = BankTransactionHistoryViewHelper(
                position,
                transactionList?.get(position), viewModel,this@BankTransactionHistoryAdapter
            )
            mRowItemBinding!!.viewHelper = mViewHelper
            mViewHelper.init()
            mRowItemBinding.executePendingBindings()

        }

    }

    /**
     * This will set the data in the list in adapter
     */
    fun setList(transactionList1: List<BankTransactionHistoryResponseDetails>?) {
        try {
            if (transactionList1 != null) {
                transactionList1?.forEach {
                    transactionList!!.add(it)
                }
            } else {

                transactionList!!.clear()
            }

            Log.d("akiakjdi", transactionList1?.size.toString())
            notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}
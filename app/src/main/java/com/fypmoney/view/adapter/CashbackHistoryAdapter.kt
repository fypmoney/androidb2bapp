package com.fypmoney.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.base.BaseViewHolder
import com.fypmoney.databinding.CashbackHistoryRowItemBinding
import com.fypmoney.model.BankTransactionHistoryResponseDetails
import com.fypmoney.model.CashbackWonResponse
import com.fypmoney.view.rewardsAndWinnings.viewModel.RewardsCashbackwonVM
import com.fypmoney.viewhelper.CashBackHistoryViewHelper


/**
 * This adapter class is used to handle bank transaction history
 */
class CashbackHistoryAdapter(var viewModel: RewardsCashbackwonVM) :
    RecyclerView.Adapter<BaseViewHolder>() {
    var transactionList: ArrayList<BankTransactionHistoryResponseDetails>? = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val mRowBinding = CashbackHistoryRowItemBinding.inflate(
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
        private val mRowItemBinding: CashbackHistoryRowItemBinding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        lateinit var mViewHelper: CashBackHistoryViewHelper

        override fun onBind(position: Int) {
            mViewHelper = CashBackHistoryViewHelper(
                position,
                transactionList?.get(position), viewModel, this@CashbackHistoryAdapter
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
                transactionList1.forEach {
                    transactionList!!.add(it)
                }
            } else {

                transactionList!!.clear()
            }
            notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}
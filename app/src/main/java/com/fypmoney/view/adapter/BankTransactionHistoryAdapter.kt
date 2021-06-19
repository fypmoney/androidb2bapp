package com.fypmoney.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.base.BaseViewHolder
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.databinding.BankTransactionHistoryRowItemBinding
import com.fypmoney.viewhelper.BankTransactionHistoryViewHelper
import com.fypmoney.viewmodel.BankTransactionHistoryViewModel


/**
 * This adapter class is used to handle contacts
 */
class BankTransactionHistoryAdapter(var viewModel: BankTransactionHistoryViewModel) :
    RecyclerView.Adapter<BaseViewHolder>() {
    var contactList: ArrayList<ContactEntity>? = ArrayList()
    var newSearchList: ArrayList<ContactEntity>? = ArrayList()
    var newContactList: ArrayList<ContactEntity>? = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val mRowBinding = BankTransactionHistoryRowItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(mRowBinding)


    }

    override fun getItemCount(): Int {
        return contactList!!.size
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
                contactList?.get(position), viewModel
            )
            mRowItemBinding!!.viewHelper = mViewHelper
            mViewHelper.init()
            mRowItemBinding.executePendingBindings()

        }

    }

    /**
     * This will set the data in the list in adapter
     */
    fun setList(contactList1: List<ContactEntity>?) {
        try {
            contactList!!.clear()
            contactList1?.forEach {
                contactList!!.add(it)
            }
            notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}
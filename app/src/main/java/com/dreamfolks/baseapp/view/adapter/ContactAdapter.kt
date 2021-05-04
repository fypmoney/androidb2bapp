package com.dreamfolks.baseapp.view.adapter


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dreamfolks.baseapp.base.BaseViewHolder
import com.dreamfolks.baseapp.database.entity.ContactEntity
import com.dreamfolks.baseapp.databinding.ContactRowItemBinding
import com.dreamfolks.baseapp.model.ContactRequestDetails
import com.dreamfolks.baseapp.viewhelper.ContactViewHelper
import com.dreamfolks.baseapp.viewmodel.ContactViewModel
import java.lang.Exception


/**
 * This adapter class is used to handle contacts
 */
class ContactAdapter(var viewModel: ContactViewModel) :
    RecyclerView.Adapter<BaseViewHolder>() {
    var contactList: ArrayList<ContactEntity>? = ArrayList()
    var newContactList: ArrayList<ContactEntity>? = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val mRowBinding = ContactRowItemBinding.inflate(
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
        private val mRowItemBinding: ContactRowItemBinding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        lateinit var mViewHelper: ContactViewHelper

        override fun onBind(position: Int) {
            mViewHelper = ContactViewHelper(
                this@ContactAdapter,
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
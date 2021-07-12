package com.fypmoney.view.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.base.BaseViewHolder
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.databinding.ContactRowItemBinding
import com.fypmoney.util.Utility
import com.fypmoney.viewhelper.ContactViewHelper
import com.fypmoney.viewmodel.ContactViewModel


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
                position,
                contactList?.get(position), viewModel
            )
            mRowItemBinding!!.viewHelper = mViewHelper
            if (contactList?.get(position)?.profilePicResourceId.isNullOrEmpty()) {
                mRowItemBinding.ivServiceLogo.visibility = View.GONE
                mRowItemBinding.ivServiceLogo1.visibility = View.VISIBLE
            }
            else
            {
                mRowItemBinding.ivServiceLogo.visibility = View.VISIBLE
                mRowItemBinding.ivServiceLogo1.visibility = View.GONE

            }
            Utility.setImageUsingGlide(
                url = contactList?.get(position)?.profilePicResourceId,
                imageView = mRowItemBinding.ivServiceLogo
            )
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
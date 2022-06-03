package com.fypmoney.view.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.base.BaseViewHolder
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.databinding.ContactListRowItemBinding
import com.fypmoney.util.Utility
import com.fypmoney.view.contacts.viewmodel.CopyPayToContactsActivityCM
import com.fypmoney.viewhelper.ContactListViewHelper
import com.fypmoney.view.contacts.viewmodel.PayToContactsActivityVM


/**
 * This adapter class is used to handle contacts
 */
class ContactListAdapter(var viewModel: CopyPayToContactsActivityCM, var userId: Long) :
    RecyclerView.Adapter<BaseViewHolder>() {
    var contactList: ArrayList<ContactEntity>? = ArrayList()
    var newSearchList: ArrayList<ContactEntity>? = ArrayList()
    var newContactList: ArrayList<ContactEntity>? = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val mRowBinding = ContactListRowItemBinding.inflate(
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
        private val mRowItemBinding: ContactListRowItemBinding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        lateinit var mViewHelper: ContactListViewHelper

        override fun onBind(position: Int) {
            mViewHelper = ContactListViewHelper(
                position,
                contactList?.get(position), viewModel,
                userId
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
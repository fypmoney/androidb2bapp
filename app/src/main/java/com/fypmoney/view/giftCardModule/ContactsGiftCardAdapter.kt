package com.fypmoney.view.giftCardModule


import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.R
import com.fypmoney.base.BaseViewHolder
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.databinding.ContactRowGiftCardItemBinding
import com.fypmoney.databinding.ContactRowItemBinding
import com.fypmoney.util.Utility
import com.fypmoney.viewhelper.ContactViewHelper
import com.fypmoney.viewmodel.ContactViewModel
import com.fypmoney.viewmodel.PurchaseGiftViewModel


/**
 * This adapter class is used to handle contacts
 */
class ContactsGiftCardAdapter(var viewModel: PurchaseGiftViewModel, var userId: Long) :
    RecyclerView.Adapter<BaseViewHolder>() {

    var contactList: ArrayList<ContactEntity>? = ArrayList()
    var newContactList: ArrayList<ContactEntity>? = ArrayList()
    var newSearchList: ArrayList<ContactEntity>? = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val mRowBinding = ContactRowGiftCardItemBinding.inflate(
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
        private val mRowItemBinding: ContactRowGiftCardItemBinding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        lateinit var mViewHelper: ContactGiftViewHelper

        override fun onBind(position: Int) {


            mViewHelper = ContactGiftViewHelper(
                position,
                contactList?.get(position), viewModel,
                userId
            )
            mRowItemBinding!!.viewHelper = mViewHelper
            if (contactList?.get(position)?.profilePicResourceId.isNullOrEmpty()) {
                mRowItemBinding.ivServiceLogo.visibility = View.GONE
                mRowItemBinding.ivServiceLogo1.visibility = View.VISIBLE
            } else {
                mRowItemBinding.ivServiceLogo.visibility = View.VISIBLE
                mRowItemBinding.ivServiceLogo1.visibility = View.GONE

            }
            Utility.setImageUsingGlide(
                url = contactList?.get(position)?.profilePicResourceId,
                imageView = mRowItemBinding.ivServiceLogo
            )

            mRowItemBinding.bgCard.setBackgroundResource(if (viewModel.selectedPosition.get() === position) R.drawable.background_grey_contact_selected else R.drawable.background_grey)
            mRowItemBinding.bgCard.setOnClickListener {
                viewModel.selectedPosition.set(position)
                viewModel.onItemClicked.value = contactList?.get(position)
                notifyDataSetChanged()
            }

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
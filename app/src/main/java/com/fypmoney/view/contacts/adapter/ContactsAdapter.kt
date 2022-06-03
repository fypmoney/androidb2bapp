package com.fypmoney.view.contacts.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.R
import com.fypmoney.bindingAdapters.loadImage
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.databinding.ItemContactListBinding
import com.fypmoney.extension.executeAfter
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible

class ContactsAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val onContactClick:(contactUiModel:ContactsUiModel)->Unit
):ListAdapter<ContactsUiModel, ContactsAdapter.ContactsItemVH>(ContactsDiffUtils) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsItemVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemContactListBinding.inflate(inflater, parent, false)
        return ContactsItemVH(
            binding,
            lifecycleOwner,
            onContactClick,
        )
    }

    override fun onBindViewHolder(holder: ContactsItemVH, position: Int) {
        holder.bind(getItem(position))
    }

    class ContactsItemVH(
        private val binding:ItemContactListBinding,
        private val lifecycleOwner: LifecycleOwner,
        private val onContactClick:(contactUiModel:ContactsUiModel)->Unit
    ):RecyclerView.ViewHolder(binding.root){
        fun bind(contact:ContactsUiModel){
            binding.executeAfter {
                lifecycleOwner = this@ContactsItemVH.lifecycleOwner
                contact.imageUrl?.let {
                    loadImage(
                        contactIv,
                        it,
                        ContextCompat.getDrawable(contactIv.context, R.drawable.ic_profile_img),
                        true
                    )
                }
                contactNameTv.text = contact.firstAndLastName
                contactNumberTv.text = contact.mobileNumber
                if(contact.isAppUser == true){
                    fypUserFlagIv.toVisible()
                    fyperTv.toVisible()
                    inviteTv.toGone()
                }else{
                    fypUserFlagIv.toGone()
                    fyperTv.toGone()
                    inviteTv.toVisible()
                }
                contactCl.setOnClickListener {
                    onContactClick(contact)
                }
            }
        }
    }
}

object ContactsDiffUtils : DiffUtil.ItemCallback<ContactsUiModel>() {

    override fun areItemsTheSame(oldItem: ContactsUiModel, newItem: ContactsUiModel): Boolean {
        return (oldItem.mobileNumber == newItem.mobileNumber)
    }

    override fun areContentsTheSame(oldItem: ContactsUiModel, newItem: ContactsUiModel): Boolean {
        return ((oldItem.imageUrl == newItem.imageUrl) && (oldItem.mobileNumber == newItem.mobileNumber)
                && (oldItem.firstAndLastName == newItem.firstAndLastName) &&
                (oldItem.isAppUser == newItem.isAppUser))
    }
}


@Keep
data class ContactsUiModel(
    var contactId:String?,
    var imageUrl:String?,
    var firstAndLastName:String?,
    var mobileNumber:String?,
    var isAppUser:Boolean? = false,
){
    companion object{
        fun contactEntityToContactUiModel(contactEntity: ContactEntity):ContactsUiModel{
            return ContactsUiModel(
                contactId = contactEntity.userId,
                imageUrl = contactEntity.profilePicResourceId,
                firstAndLastName = contactEntity.firstName+" "+contactEntity.lastName,
                mobileNumber = contactEntity.contactNumber,
                isAppUser = contactEntity.isAppUser
            )
        }
    }
}
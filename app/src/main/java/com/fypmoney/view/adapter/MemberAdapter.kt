package com.fypmoney.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewHolder
import com.fypmoney.bindingAdapters.loadImage
import com.fypmoney.database.entity.MemberEntity
import com.fypmoney.databinding.AddMemberLayoutBinding
import com.fypmoney.databinding.MemberRowItemBinding
import com.fypmoney.viewhelper.MemberViewHelper


/**
 * This adapter class is used to handle members
 */
class MemberAdapter(
    var onMemberItemClickListener: OnMemberItemClickListener,
    var familyMember: OnFamilyMemberClickListener
) :
    RecyclerView.Adapter<BaseViewHolder>() {
    var memberList: ArrayList<MemberEntity>? = ArrayList()
    private val typeAdd = 1
    private val typeMember = 2
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        when (viewType) {
            typeMember -> {
                val mRowBinding = MemberRowItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
                return ViewHolder(mRowBinding)
            }
            else -> {

                val mRowBinding = AddMemberLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
                return AddViewHolder(mRowBinding)
            }
        }


    }

    override fun getItemCount(): Int {
        return memberList!!.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        return holder.onBind(position)
    }

    inner class ViewHolder(
        private val mRowItemBinding: MemberRowItemBinding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        private lateinit var mViewHelper: MemberViewHelper
        override fun onBind(position: Int) {
            mViewHelper = MemberViewHelper(
                position,
                memberList?.get(position), onMemberItemClickListener, familyMember
            )
            mRowItemBinding?.memberProfilePic?.let {
                loadImage(
                    it, memberList?.get(position)?.profilePicResourceId,
                    this.mRowItemBinding?.memberProfilePic?.let {
                        ContextCompat.getDrawable(
                            it.context,
                            R.drawable.ic_profile_img
                        )
                    }, true
                )
            }

            mRowItemBinding!!.viewHelper = mViewHelper
            mViewHelper.init()
            mRowItemBinding.executePendingBindings()
        }

    }

    inner class AddViewHolder(
        private val mRowItemBinding: AddMemberLayoutBinding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        private lateinit var mViewHelper: MemberViewHelper
        override fun onBind(position: Int) {
            mViewHelper = MemberViewHelper(
                position,
                memberList?.get(position), onMemberItemClickListener, familyMember
            )
            mRowItemBinding!!.viewHelper = mViewHelper
            mViewHelper.init()
            mRowItemBinding.executePendingBindings()
        }

    }

    /**
     * This will set the data in the list in adapter
     */
    fun setList(memberList1: List<MemberEntity>?) {
        memberList!!.clear()
        val memberEntity = MemberEntity()
        memberEntity.name = PockketApplication.instance.getString(R.string.add_member_heading)
        memberList?.add(memberEntity)

        memberList1!!.forEach {
            memberList!!.add(it)
        }
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> {
                typeAdd
            }
            else -> {
                typeMember
            }
        }

    }

    interface OnMemberItemClickListener {
        fun onItemClick(position: Int)
    }

    interface OnFamilyMemberClickListener {
        fun onItemClick(position: MemberEntity?)
    }

    /**
     * This will set the data in the list in adapter
     */
    fun addFirstElement() {
        memberList!!.clear()
        val memberEntity = MemberEntity()
        memberEntity.name = PockketApplication.instance.getString(R.string.add_member_heading)
        memberList?.add(memberEntity)

        notifyItemInserted(0)
    }


}
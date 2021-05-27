package com.fypmoney.view.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.base.BaseViewHolder
import com.fypmoney.database.entity.MemberEntity
import com.fypmoney.databinding.MemberHorizontalRowItemBinding
import com.fypmoney.viewhelper.MemberHorizontalViewHelper


/**
 * This adapter class is used to handle members
 */
class MemberAdapterViewAll:
    RecyclerView.Adapter<BaseViewHolder>() {
    var memberList: ArrayList<MemberEntity>? = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val mRowBinding = MemberHorizontalRowItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(mRowBinding)


    }

    override fun getItemCount(): Int {
        return memberList!!.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        return holder.onBind(position)
    }

    inner class ViewHolder(
        private val mRowItemBinding: MemberHorizontalRowItemBinding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        private lateinit var mViewHelper: MemberHorizontalViewHelper
        override fun onBind(position: Int) {
            mViewHelper = MemberHorizontalViewHelper(
                memberList?.get(position)
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
        memberList1!!.forEach {
            memberList!!.add(it)
        }
        notifyDataSetChanged()
    }

    interface OnMemberItemClickListener {
        fun onMemberClick(id: Int, memberDetails:MemberEntity)
    }

}
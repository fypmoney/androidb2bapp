package com.fypmoney.view.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.base.BaseViewHolder
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.databinding.ContactRowItemBinding
import com.fypmoney.databinding.RelationRowItemBinding
import com.fypmoney.databinding.RelationRowTaskItemBinding
import com.fypmoney.model.RelationModel
import com.fypmoney.viewhelper.ContactViewHelper
import com.fypmoney.viewhelper.RelationTaskViewHelper
import com.fypmoney.viewhelper.RelationViewHelper
import com.fypmoney.viewmodel.AddMemberViewModel
import com.fypmoney.viewmodel.AddTaskViewModel
import com.fypmoney.viewmodel.ContactViewModel


/**
 * This adapter class is used to handle contacts
 */
class RelationTaskAdapter(var viewModel: AddTaskViewModel) :
    RecyclerView.Adapter<BaseViewHolder>() {
    var relationList: ArrayList<RelationModel>? = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val mRowBinding = RelationRowTaskItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(mRowBinding)

    }

    override fun getItemCount(): Int {
        return relationList!!.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        return holder.onBind(position)
    }

    inner class ViewHolder(
        private val mRowItemBinding: RelationRowTaskItemBinding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        lateinit var mViewHelper: RelationTaskViewHelper

        override fun onBind(position: Int) {
            mViewHelper = RelationTaskViewHelper(this@RelationTaskAdapter,
                position, relationList?.get(position),viewModel
            )
            mRowItemBinding!!.viewHelper = mViewHelper
            mViewHelper.init()
            mRowItemBinding.executePendingBindings()

        }

    }

    /**
     * This will set the data in the list in adapter
     */
    fun setList(relationList1: List<RelationModel>?) {
        try {
            relationList!!.clear()
            relationList1?.forEach {
                relationList!!.add(it)
            }
            notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
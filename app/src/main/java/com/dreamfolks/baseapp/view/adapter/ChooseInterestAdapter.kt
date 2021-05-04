package com.dreamfolks.baseapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dreamfolks.baseapp.base.BaseViewHolder
import com.dreamfolks.baseapp.databinding.ChooseInterestRowItemBinding
import com.dreamfolks.baseapp.model.InterestEntity
import com.dreamfolks.baseapp.viewhelper.ChooseInterestViewHelper
import com.dreamfolks.baseapp.viewmodel.SelectInterestViewModel

/**
 * This adapter class is used to handle interest of the user
 */
class ChooseInterestAdapter(var viewModel: SelectInterestViewModel) :
    RecyclerView.Adapter<BaseViewHolder>() {
    var chooseInterestList: ArrayList<InterestEntity>? = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val mRowBinding = ChooseInterestRowItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(mRowBinding)


    }

    override fun getItemCount(): Int {
        return chooseInterestList!!.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        return holder.onBind(position)
    }

    inner class ViewHolder(
        private val mRowItemBinding: ChooseInterestRowItemBinding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        private lateinit var mViewHelper: ChooseInterestViewHelper
        override fun onBind(position: Int) {
            mViewHelper = ChooseInterestViewHelper(
                position,
                chooseInterestList?.get(position),viewModel
            )
            mRowItemBinding!!.viewHelper = mViewHelper
            mRowItemBinding.viewModel = viewModel
            mViewHelper.init()
            mRowItemBinding.executePendingBindings()
        }

    }

    /**
     * This will set the data in the list in adapter
     */
    fun setList(chooseInterestList1: List<InterestEntity>?) {
        chooseInterestList!!.clear()
        chooseInterestList1!!.forEach {
            chooseInterestList!!.add(it)
        }
        notifyDataSetChanged()
    }


}
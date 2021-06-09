package com.fypmoney.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.base.BaseViewHolder
import com.fypmoney.databinding.SavedCardRowItemBinding
import com.fypmoney.model.SavedCardResponseDetails
import com.fypmoney.viewhelper.SavedCardViewHelper


/**
 * This adapter class is used to handle saved card list in add money
 */
class SavedCardsAdapter:
    RecyclerView.Adapter<BaseViewHolder>() {
    var cardList: ArrayList<SavedCardResponseDetails>? = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val mRowBinding = SavedCardRowItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(mRowBinding)


    }

    override fun getItemCount(): Int {
        return cardList!!.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        return holder.onBind(position)
    }

    inner class ViewHolder(
        private val mRowItemBinding: SavedCardRowItemBinding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        private lateinit var mViewHelper: SavedCardViewHelper
        override fun onBind(position: Int) {
            mViewHelper = SavedCardViewHelper(
                cardList?.get(position)
            )
            mRowItemBinding!!.viewHelper = mViewHelper
            mRowItemBinding.executePendingBindings()
        }

    }

    /**
     * This will set the data in the list in adapter
     */
    fun setList(savedCardList1: List<SavedCardResponseDetails>?) {
        cardList!!.clear()
        savedCardList1!!.forEach {
            cardList!!.add(it)
        }
        notifyDataSetChanged()
    }


}
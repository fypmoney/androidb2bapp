package com.fypmoney.view.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.base.BaseViewHolder
import com.fypmoney.databinding.RewardHistoryRowItemBinding
import com.fypmoney.model.GetRewardsHistoryResponseDetails
import com.fypmoney.viewhelper.RewardsHistoryViewHelper


/**
 * This adapter class is used to handle rewards history
 */
class RewardsHistoryAdapter() :
    RecyclerView.Adapter<BaseViewHolder>() {
    var rewardsList: ArrayList<GetRewardsHistoryResponseDetails>? = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val mRowBinding = RewardHistoryRowItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )

        return ViewHolder(mRowBinding)
    }

    override fun getItemCount(): Int {
        return rewardsList!!.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        return holder.onBind(position)
    }

    inner class ViewHolder(
        private val mRowItemBinding: RewardHistoryRowItemBinding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        lateinit var mViewHelper: RewardsHistoryViewHelper

        override fun onBind(position: Int) {
            mViewHelper = RewardsHistoryViewHelper(
                rewardsList?.get(position)
            )
            mRowItemBinding!!.viewHelper = mViewHelper
            mRowItemBinding.executePendingBindings()

        }

    }

    /**
     * This will set the data in the list in adapter
     */
    fun setList(rewardsList1: List<GetRewardsHistoryResponseDetails>?) {
        try {
            rewardsList!!.clear()
            rewardsList1?.forEach {
                rewardsList!!.add(it)
            }
            notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}
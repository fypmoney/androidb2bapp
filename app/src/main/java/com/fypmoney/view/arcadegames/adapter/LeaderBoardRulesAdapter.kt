package com.fypmoney.view.arcadegames.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fypmoney.databinding.ItemLeaderboardRulesBinding

class LeaderBoardRulesAdapter(private val leaderBoardImagesList: MutableList<LeaderBoardRulesUiModel>) :
    RecyclerView.Adapter<LeaderBoardRulesAdapter.LeaderBoardRulesVH>() {
//    private val leaderBoardRulesImages = mutableListOf<LeaderBoardRulesUiModel>()

    class LeaderBoardRulesVH(private val binding: ItemLeaderboardRulesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LeaderBoardRulesUiModel) {
            Glide.with(binding.ivLeaderBoardRules.context).load(item.rulesImage)
                .into(binding.ivLeaderBoardRules)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderBoardRulesVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemLeaderboardRulesBinding.inflate(inflater, parent, false)
        return LeaderBoardRulesVH(binding)
    }

    override fun onBindViewHolder(holder: LeaderBoardRulesVH, position: Int) {
        return holder.bind(leaderBoardImagesList[position])
    }

    override fun getItemCount(): Int {
        return leaderBoardImagesList.size
    }

//    fun setList(rulesImagesList: List<LeaderBoardRulesUiModel>) {
//        for (item in rulesImagesList) {
//            leaderBoardRulesImages.add(item)
//        }
//        notifyDataSetChanged()
//    }
}

@Keep
data class LeaderBoardRulesUiModel(
    var rulesImage: String?
)
//{
//    companion object {
//        fun fromLeaderBoardRulesItem(
//            leaderBoardRulesItem: LeaderBoardRulesItem
//        ): LeaderBoardRulesUiModel {
//            val rulesImage = leaderBoardRulesItem.rulesImage
//
//            return LeaderBoardRulesUiModel(
//                rulesImage = rulesImage
//            )
//        }
//    }
//}

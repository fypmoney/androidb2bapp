package com.fypmoney.view.arcadegames.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.R
import com.fypmoney.databinding.ItemLeaderboardBinding
import com.fypmoney.view.arcadegames.model.LeaderBoardListItem

class LeaderBoardAdapter :
    ListAdapter<LeaderBoardUiModel, LeaderBoardAdapter.LeaderBoardVH>(LeaderBoardDiffUtils) {
    class LeaderBoardVH(private val binding: ItemLeaderboardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LeaderBoardUiModel) {
            when (item.userPosition) {
                "1st" -> {
                    binding.tvLeaderBoardPosition.text = item.userPosition.toString()
                    binding.relativePositionView.setBackgroundColor(
                        binding.relativePositionView.context.resources.getColor(
                            R.color.reward_golden_tickets_text
                        )
                    )
                    binding.tvLeaderBoardPosition.setTextColor(binding.tvLeaderBoardPosition.context.resources.getColor(R.color.black_grey_txt_color))
                }
                "2nd" -> {
                    binding.tvLeaderBoardPosition.text = item.userPosition.toString()
                    binding.relativePositionView.setBackgroundColor(
                        binding.relativePositionView.context.resources.getColor(
                            R.color.rewardLeaderBoardSilver
                        )
                    )
                    binding.tvLeaderBoardPosition.setTextColor(binding.tvLeaderBoardPosition.context.resources.getColor(R.color.black_grey_txt_color))
                }
                "3rd" -> {
                    binding.tvLeaderBoardPosition.text = item.userPosition.toString()
                    binding.relativePositionView.setBackgroundColor(
                        binding.relativePositionView.context.resources.getColor(
                            R.color.rewardLeaderBoardBronze
                        )
                    )
                    binding.tvLeaderBoardPosition.setTextColor(binding.tvLeaderBoardPosition.context.resources.getColor(R.color.black_grey_txt_color))
                }
                else -> {
                    binding.tvLeaderBoardPosition.text = item.userPosition.toString()
                    binding.relativePositionView.setBackgroundColor(
                        binding.relativePositionView.context.resources.getColor(
                            R.color.transparent
                        )
                    )

                    binding.tvLeaderBoardPosition.setTextColor(binding.tvLeaderBoardPosition.context.resources.getColor(R.color.white))
                }
            }

            binding.tvLeaderBoardUserName.text = item.userName
            binding.tvMultipleTicketValue.text = item.userTicketsCount.toString()


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderBoardVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemLeaderboardBinding.inflate(inflater, parent, false)
        return LeaderBoardVH(binding)
    }

    override fun onBindViewHolder(holder: LeaderBoardVH, position: Int) {
        holder.bind(getItem(position))
    }
}

data class LeaderBoardUiModel(
    var userName: String?,
    var userPosition: String?,
    var userTicketsCount: Int?
) {
    companion object {
        fun fromLeaderBoardItem(
            context: Context,
            leaderBoardListItem: LeaderBoardListItem
        ): LeaderBoardUiModel {

            val userName = leaderBoardListItem.userName
            val userPosition = leaderBoardListItem.position
            val userTicketsCount = leaderBoardListItem.goldenTickets
            return LeaderBoardUiModel(
                userName = userName,
                userPosition = userPosition,
                userTicketsCount = userTicketsCount
            )
        }
    }
}

object LeaderBoardDiffUtils : DiffUtil.ItemCallback<LeaderBoardUiModel>() {
    override fun areItemsTheSame(
        oldItem: LeaderBoardUiModel,
        newItem: LeaderBoardUiModel
    ): Boolean {
        return (oldItem ==  newItem)
    }

    override fun areContentsTheSame(
        oldItem: LeaderBoardUiModel,
        newItem: LeaderBoardUiModel
    ): Boolean {
        return ((oldItem.userName ==  newItem.userName) && (oldItem.userPosition == newItem.userPosition) && (oldItem.userTicketsCount == oldItem.userTicketsCount))
    }

}
package com.fypmoney.view.rewardsAndWinnings.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.R
import com.fypmoney.model.HistoryItem
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.rewardsAndWinnings.interfaces.ListRewardsItemClickListener
import kotlinx.android.synthetic.main.reward_history_item_leaderboard.view.*

import java.util.*


class RewardsHistoryLeaderboardAdapter(
    val items: ArrayList<HistoryItem>,
    val context: Context,
    val clickInterface: ListRewardsItemClickListener
) : RecyclerView.Adapter<RewardsHistoryLeaderboardAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.reward_history_item_leaderboard, parent, false)
        )

    }

    override fun getItemCount(): Int {

        return items.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        (items[position].points.toString() + " Mynts").also { holder.numberofMynts.text = it }
        holder.desc.text = items[position].eventDescription.toString()




        if (items[position].isFullFilled == AppConstants.NO) {
            holder.note.visibility = View.INVISIBLE


            holder.amount.visibility = View.INVISIBLE

            holder.won_tv.visibility = View.INVISIBLE

            holder.status_tv.visibility = View.VISIBLE
        } else {
            holder.status_tv.visibility = View.INVISIBLE
            if (items[position].noOfJackpotTicket == null) {
                if (items[position].cashbackWonForProduct != null && items[position].cashbackWonForProduct!! > 0) {
                    holder.note.visibility = View.VISIBLE


                    holder.amount.visibility = View.VISIBLE

                    holder.won_tv.visibility = View.VISIBLE
                    holder.status_tv.visibility = View.INVISIBLE

                    holder.amount.text =
                        " ₹" + Utility.convertToRs(items[position].cashbackWonForProduct.toString())


                    holder.note.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.cash
                        )
                    )

                } else {
                    holder.note.visibility = View.INVISIBLE
//           holder.amount.visibility=View.INVISIBLE

                    if (items[position].transactionType == AppConstants.TRANS_TYPE_EARN) {
                        holder.amount.visibility = View.INVISIBLE

                    }
                    holder.amount.text = "₹0"
//           holder.amount.visibility=View.INVISIBLE
                    holder.amount.visibility = View.VISIBLE

                    holder.won_tv.visibility = View.VISIBLE
//            holder.amount.visibility = View.INVISIBLE
                    holder.status_tv.visibility = View.INVISIBLE


                }

            } else {
                holder.amount.text = items[position].noOfJackpotTicket.toString()
                holder.note.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ticket))
                holder.amount.text = items[position].noOfJackpotTicket.toString()


//            holder.amount.visibility = View.INVISIBLE
                holder.status_tv.visibility = View.INVISIBLE

            }
        }

        if (!items[position].fullfillmentDescription.isNullOrEmpty()) {
            holder.won_tv.text = items[position].fullfillmentDescription.toString()


        }
        if (items[position].productType == AppConstants.PRODUCT_SPIN) {
            holder.productType.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_spin_burned
                )
            )

        } else {
            holder.productType.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_scratch_card_product
                )
            )

        }
        if (items[position].transactionType == AppConstants.TRANS_TYPE_EARN) {
            holder.productType.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_mynt_coin
                )
            )
            holder.amount.visibility = View.INVISIBLE
            holder.note.visibility = View.INVISIBLE
            holder.won_tv.visibility = View.INVISIBLE

        }

        holder.card.setOnClickListener(View.OnClickListener {
            if (items[position].isFullFilled == AppConstants.NO)
                clickInterface.onItemClicked(items[position])
        })


    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        var card = view
        var desc = view.desc
        var won_tv = view.won_tv
        var amount = view.amount
        var numberofMynts = view.heading
        var status_tv = view.status_tv
        var note = view.note
        var productType = view.productType


    }
}

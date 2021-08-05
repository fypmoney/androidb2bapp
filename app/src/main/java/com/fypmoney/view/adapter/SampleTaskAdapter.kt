package com.fypmoney.view.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.fypmoney.R
import com.fypmoney.model.SampleTaskModel
import com.fypmoney.view.interfaces.ListItemClickListener
import kotlinx.android.synthetic.main.card_sample_item.view.*

import java.util.*

class SampleTaskAdapter(
    val items: ArrayList<SampleTaskModel.SampleTaskDetails>,
    val context: Context,
    var itemClickListener2: ListItemClickListener
) : androidx.recyclerview.widget.RecyclerView.Adapter<SampleTaskAdapter.BaseViewHolder<*>>() {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(baseHolder: BaseViewHolder<*>, position: Int) {

        when (baseHolder) {
            is ViewHolderAdd -> {
                var i = 0
                baseHolder.view.setOnClickListener(View.OnClickListener {
                    itemClickListener2.onCallClicked(position - 1)
                })
            }
            is ViewHolder -> {
                if (!items[position - 1].description.isNullOrEmpty()) {

                    baseHolder.heading.text = items[position - 1].description


                }
                if (!items[position - 1].amount.isNullOrEmpty()) {

                    baseHolder.amount.text = "₹" + items[position - 1].amount

                }

                baseHolder.view.setOnClickListener(View.OnClickListener {
                    itemClickListener2.onItemClicked(position - 1)
                })
                baseHolder.days.text = items[position - 1].numberOfDays.toString() + " day"

                if (position % 4 == 0) {
                    baseHolder.card_bg.background.setTint(
                        ContextCompat.getColor(
                            context,
                            R.color.color_task_card_blue
                        )
                    )
                    baseHolder.bg_text.background.setTint(
                        ContextCompat.getColor(
                            context,
                            R.color.color_task_card_blue
                        )
                    )

                } else if (position % 4 == 1) {
                    baseHolder.card_bg.background.setTint(
                        ContextCompat.getColor(
                            context,
                            R.color.color_task_card_pink
                        )
                    )
                    baseHolder.bg_text.background.setTint(
                        ContextCompat.getColor(
                            context,
                            R.color.color_task_card_pink
                        )
                    )

                } else if (position % 4 == 2) {
                    baseHolder.card_bg.background.setTint(
                        ContextCompat.getColor(
                            context,
                            R.color.color_task_card_green
                        )
                    )
                    baseHolder.bg_text.background.setTint(
                        ContextCompat.getColor(
                            context,
                            R.color.color_task_card_green
                        )
                    )

                } else if (position % 4 == 3) {
                    baseHolder.card_bg.background.setTint(
                        ContextCompat.getColor(
                            context,
                            R.color.color_task_card_orange
                        )
                    )
                    baseHolder.bg_text.background.setTint(
                        ContextCompat.getColor(
                            context,
                            R.color.color_task_card_orange
                        )
                    )

                }

            }
            else -> throw IllegalArgumentException()
        }

    }

    companion object {
        private const val TYPE_IMAGE = 0
        private const val TYPE_ADD = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            TYPE_ADD
        } else {
            TYPE_IMAGE
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {

        return when (viewType) {
            TYPE_ADD -> {
                val view =
                    LayoutInflater.from(context).inflate(R.layout.card_add_task, parent, false)
                ViewHolderAdd(view)
            }
            TYPE_IMAGE -> {
                val view =
                    LayoutInflater.from(context).inflate(R.layout.card_sample_item, parent, false)
                ViewHolder(view)
            }
            else -> throw IllegalArgumentException()

        }
    }


    // Gets the number of animals in the list
    override fun getItemCount(): Int {

        return items.size + 1


    }

    abstract class BaseViewHolder<T>(itemView: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T)
    }

    inner class ViewHolder(view: View) : BaseViewHolder<String>(view), View.OnClickListener {

        val heading = view.heading!!

        val amount = view.amount!!
        val days = view.days
        val bg_text = view.bg_text
        val view = view
        val card_bg = view.card_bg
        override fun bind(item: String) {

        }

        override fun onClick(v: View?) {


        }

        init {
            heading.setOnClickListener(this)

        }


    }

    inner class ViewHolderAdd(view: View) : BaseViewHolder<String>(view), View.OnClickListener {
        val view = view
        override fun bind(item: String) {
        }

        override fun onClick(v: View?) {
            itemClickListener2.onCallClicked(0)

        }

        init {
            view.setOnClickListener(this)
        }

    }
}



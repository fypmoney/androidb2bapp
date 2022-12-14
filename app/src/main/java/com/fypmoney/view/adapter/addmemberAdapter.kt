package com.fypmoney.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.fypmoney.R
import com.fypmoney.database.entity.MemberEntity
import com.fypmoney.util.Utility
import com.fypmoney.view.interfaces.ListItemClickListener
import kotlinx.android.synthetic.main.card_member_image.view.*

import kotlin.collections.ArrayList

class addmemberAdapter(
    val items: ArrayList<MemberEntity>,
    val context: Context,
    var itemClickListener2: ListItemClickListener
) : androidx.recyclerview.widget.RecyclerView.Adapter<addmemberAdapter.BaseViewHolder<*>>() {
    var selectedPos = -1
    override fun onBindViewHolder(baseHolder: BaseViewHolder<*>, position: Int) {

        when (baseHolder) {
            is ViewHolderAdd -> {
                var i = 0
            }
            is ViewHolder -> {
                if (!items?.get(position - 1)?.profilePicResourceId.isNullOrEmpty()) {
                    Utility.setImageUsingGlide(
                        url = items?.get(position - 1)?.profilePicResourceId,
                        imageView = baseHolder.imagee
                    )
                    baseHolder.imageViewempty.visibility = View.INVISIBLE
                    baseHolder.imagee.visibility = View.VISIBLE
                } else {
                    baseHolder.imageViewempty.visibility = View.VISIBLE
                    baseHolder.imagee.visibility = View.INVISIBLE
                }

                baseHolder.view.setOnClickListener(View.OnClickListener {
                    selectedPos = position - 1

                    itemClickListener2.onItemClicked(position - 1)
                    notifyDataSetChanged()
                })
                if (selectedPos == position - 1) {
                    itemClickListener2.onItemClicked(position - 1)
                    baseHolder.cardbackground.background =
                        ContextCompat.getDrawable(context, R.drawable.strokebutton_green_middle_18)

                } else {
                    baseHolder.cardbackground.background =
                        ContextCompat.getDrawable(context, R.drawable.white_round)

                }

                if (!items?.get(position - 1)?.name.isNullOrEmpty()) {
                    baseHolder.name_tv.text = items[position - 1].name
                }
            }
            else -> throw IllegalArgumentException()
        }

    }

    companion object {
        private const val TYPE_IMAGE = 0
        private const val TYPE_ADD = 1
        var selectedPos = -1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            TYPE_ADD
        } else {
            TYPE_IMAGE
        }



    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<*> {

        return when (viewType) {
            TYPE_ADD -> {
                val view =
                    LayoutInflater.from(context).inflate(R.layout.card_add_member, null, false)
                ViewHolderAdd(view)
            }
            TYPE_IMAGE -> {
                val view =
                    LayoutInflater.from(context).inflate(R.layout.card_member_image, null, false)
                ViewHolder(view)
            }
          else -> throw IllegalArgumentException()

      }
    }


    // Gets the number of animals in the list
    override fun getItemCount(): Int {

        return items.size + 1



    }

    abstract class BaseViewHolder<T>(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T)
    }

    inner class ViewHolder(view: View) : BaseViewHolder<String>(view),
        View.OnClickListener {

        val imagee = view.imageView2!!
        val imageViewempty = view.imageViewempty
        var name_tv = view.name_tv
        var view = view.imageView2
        var cardbackground = view.cardbackground
        override fun bind(item: String) {

        }

        override fun onClick(v: View?) {


        }

        init {
            imagee.setOnClickListener(this)

        }




    }

    inner class ViewHolderAdd(view: View) : BaseViewHolder<String>(view),
        View.OnClickListener {
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



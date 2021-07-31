package nearby.matchinteractmeet.groupalike.Profile.Trips.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.fypmoney.R
import com.fypmoney.model.AssignedTaskResponse
import com.fypmoney.view.interfaces.ListItemClickListener
import kotlinx.android.synthetic.main.card_member_image.view.*

import java.util.*

class FamilyAdapter(
    val items: ArrayList<AssignedTaskResponse>,
    val context: Context,
    itemClickListener2: ListItemClickListener
) : androidx.recyclerview.widget.RecyclerView.Adapter<FamilyAdapter.BaseViewHolder<*>>() {

    override fun onBindViewHolder(baseHolder: FamilyAdapter.BaseViewHolder<*>, position: Int) {

        when (baseHolder) {
            is FamilyAdapter.ViewHolderAdd -> {
                var i = 0
            }
            is FamilyAdapter.ViewHolder -> {

                    Glide.with(context)
                            .load("https://cdn.shopify.com/s/files/1/2166/2727/products/AAW30010_1024x1024.png?v=1503906545")
                            .into(baseHolder.imagee)

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
            FamilyAdapter.Companion.TYPE_ADD
        } else {
            FamilyAdapter.Companion.TYPE_IMAGE
        }



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FamilyAdapter.BaseViewHolder<*> {

      return  when (viewType) {
           TYPE_ADD -> {
                val view = LayoutInflater.from(context).inflate(R.layout.card_add_member, parent, false)
                 ViewHolderAdd(view)
            }
           TYPE_IMAGE -> {
                val view = LayoutInflater.from(context).inflate(R.layout.card_member_image, parent, false)
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

    inner class ViewHolder(view: View) : FamilyAdapter.BaseViewHolder<String>(view), View.OnClickListener {

        val imagee = view.imageView2!!

        override fun bind(item: String) {

        }

        override fun onClick(v: View?) {
            // Holds the TextView that will add each animal to


        }

        init {
            imagee.setOnClickListener(this)

        }




    }

    inner class ViewHolderAdd(view: View) : FamilyAdapter.BaseViewHolder<String>(view), View.OnClickListener {
        override fun bind(item: String) {
        }

        override fun onClick(v: View?) {

        }
        init {
            view.setOnClickListener(this)
        }

    }
}



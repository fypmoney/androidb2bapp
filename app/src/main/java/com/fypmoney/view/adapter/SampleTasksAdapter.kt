package nearby.matchinteractmeet.groupalike.Profile.Trips.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.fypmoney.R
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.model.AssignedTaskResponse
import com.fypmoney.util.Utility
import com.fypmoney.view.interfaces.ListItemClickListener
import kotlinx.android.synthetic.main.card_member_image.view.*

import java.util.*

class SampleTasksAdapter(
    val items: ArrayList<ContactEntity>,
    val context: Context,
   var itemClickListener2: ListItemClickListener
) : androidx.recyclerview.widget.RecyclerView.Adapter<SampleTasksAdapter.BaseViewHolder<*>>() {

    override fun onBindViewHolder(baseHolder: SampleTasksAdapter.BaseViewHolder<*>, position: Int) {

        when (baseHolder) {
            is SampleTasksAdapter.ViewHolderAdd -> {
                var i = 0
            }
            is SampleTasksAdapter.ViewHolder -> {
                if (items?.get(position-1)?.profilePicResourceId.isNullOrEmpty()) {
                    Utility.setImageUsingGlide(
                        url = items?.get(position-1)?.profilePicResourceId,
                        imageView = baseHolder.imagee
                    )
                }
                else
                {
                    Glide.with(context)
                        .load("https://cdn.shopify.com/s/files/1/2166/2727/products/AAW30010_1024x1024.png?v=1503906545")
                        .into(baseHolder.imagee)
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
            SampleTasksAdapter.Companion.TYPE_ADD
        } else {
            SampleTasksAdapter.Companion.TYPE_IMAGE
        }



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SampleTasksAdapter.BaseViewHolder<*> {

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

    inner class ViewHolder(view: View) : SampleTasksAdapter.BaseViewHolder<String>(view), View.OnClickListener {

        val imagee = view.imageView2!!

        override fun bind(item: String) {

        }

        override fun onClick(v: View?) {



        }

        init {
            imagee.setOnClickListener(this)

        }




    }

    inner class ViewHolderAdd(view: View) : SampleTasksAdapter.BaseViewHolder<String>(view), View.OnClickListener {
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



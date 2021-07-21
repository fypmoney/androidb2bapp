package com.fypmoney.view.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.base.BaseViewHolder
import com.fypmoney.databinding.BenefitsRowItemBinding
import com.fypmoney.viewhelper.BenefitsViewHelper


/**
 * This adapter class is used to handle benefits
 */
class BenefitsAdapter:
    RecyclerView.Adapter<BaseViewHolder>() {
    var imageList: ArrayList<Int>? = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val mRowBinding = BenefitsRowItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(mRowBinding)


    }

    override fun getItemCount(): Int {
        return imageList!!.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        return holder.onBind(position)
    }

    inner class ViewHolder(
        private val mRowItemBinding: BenefitsRowItemBinding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        private lateinit var mViewHelper: BenefitsViewHelper
        override fun onBind(position: Int) {
            mViewHelper = BenefitsViewHelper(imageList!![position]
            )
            mRowItemBinding!!.viewHelper = mViewHelper
            mRowItemBinding.executePendingBindings()
        }

    }

    /**
     * This will set the data in the list in adapter
     */
    fun setList(images: List<Int>?) {
        imageList!!.clear()
        images!!.forEach {
            imageList!!.add(it)
        }
        notifyDataSetChanged()
    }


}
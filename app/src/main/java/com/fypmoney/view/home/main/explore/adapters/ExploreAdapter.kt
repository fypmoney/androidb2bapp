package com.fypmoney.view.home.main.explore.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.base.BaseViewHolder
import com.fypmoney.databinding.*
import com.fypmoney.view.home.main.explore.`interface`.ExploreItemClickListener
import com.fypmoney.view.home.main.explore.model.SectionContentItem
import com.fypmoney.view.home.main.explore.view.ExploreViewHelper
import com.fypmoney.view.home.main.explore.viewmodel.ExploreFragmentVM


/**
 * This adapter class is used to handle feeds
 */
class ExploreAdapter(
    var viewModel: ExploreFragmentVM,
    var onExploreItemClickListener: ExploreItemClickListener,
    val exploreList: List<SectionContentItem?>?,
    val context: Context,
    val scale: Float
) :
    RecyclerView.Adapter<BaseViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {

                val mRowBinding = DynamicCardBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
        return DynamicCardViewHolder(mRowBinding)

    }

    override fun getItemCount(): Int {
        return exploreList!!.size
    }


    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {

        return holder.onBind(position)
    }


    inner class DynamicCardViewHolder(
        private val mRowItemBinding: DynamicCardBinding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        private lateinit var mViewHelper: ExploreViewHelper
        override fun onBind(position: Int) {
            mViewHelper = ExploreViewHelper(
                position,
                exploreList?.get(position), onExploreItemClickListener
            )
            mRowItemBinding!!.viewHelper = mViewHelper
            mRowItemBinding.viewModel = viewModel


            val params: ViewGroup.LayoutParams =
                mRowItemBinding.image.layoutParams as ViewGroup.LayoutParams

            params.width = (scale * exploreList?.get(position)?.contentDimensionX!!).toInt()
            params.height = (scale * exploreList?.get(position)?.contentDimensionY!!).toInt()
            mRowItemBinding.image.layoutParams = params



            mRowItemBinding.executePendingBindings()

        }


    }






    interface OnFeedItemClickListener {
        fun onFeedClick(position: Int, feedDetails: SectionContentItem)
    }

}
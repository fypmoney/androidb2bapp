package com.fypmoney.view.insights.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.R
import com.fypmoney.databinding.ItemSpendsIncomeBinding
import com.fypmoney.extension.executeAfter
import com.fypmoney.util.Utility
import com.fypmoney.view.insights.model.CategoryItem
import kotlin.math.roundToInt

class SpendAndIncomeCategoryListAdapter(
    private val lifeCycleOwner:LifecycleOwner,
    private val onCategoryClick:(categoryCode:String,categoryName:String)->Unit
):ListAdapter<SpendAndIncomeCategoryUiModel, SpendAndIncomeCategoryListAdapter.SpendAndIncomeCategoryItemVH>(SpendAndIncomeCategoryDiffUtils) {

    /**
     * Called when RecyclerView needs a new [ViewHolder] of the given type to represent
     * an item.
     *
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     *
     *
     * The new ViewHolder will be used to display items of the adapter using
     * [.onBindViewHolder]. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary [View.findViewById] calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see .getItemViewType
     * @see .onBindViewHolder
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SpendAndIncomeCategoryItemVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSpendsIncomeBinding.inflate(inflater,parent,false)
        return SpendAndIncomeCategoryItemVH( binding,  lifeCycleOwner, onCategoryClick)
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the [ViewHolder.itemView] to reflect the item at the given
     * position.
     *
     *
     * Note that unlike [android.widget.ListView], RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the `position` parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use [ViewHolder.getBindingAdapterPosition] which
     * will have the updated adapter position.
     *
     * Override [.onBindViewHolder] instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: SpendAndIncomeCategoryItemVH, position: Int) {
        holder.onBind(getItem(position))
    }



    class SpendAndIncomeCategoryItemVH(
        private val binding: ItemSpendsIncomeBinding,
        private val lifeCycleOwner: LifecycleOwner,
        private val onCategoryClick:(categoryCode:String,categoryName:String)->Unit
    ): RecyclerView.ViewHolder(binding.root){
        fun onBind(item:SpendAndIncomeCategoryUiModel){
            binding.executeAfter {
                lifecycleOwner = lifeCycleOwner
                cvSpendsAndIncome.setCardBackgroundColor(Color.parseColor(item.categoryColor))
                Utility.setImageUsingGlideWithShimmerPlaceholder(
                    context = ivCategorey.context,
                    url = item.categoryIcon,
                    imageView = ivCategorey
                )
                tvCategoryName.text = item.categoryName
                tvAmount.text = item.amount?:"₹ 0"
                tvPercentage.text = item.percentage?:"0 %"
                cpiIncomeSpendsPercentage.progress = item.progress?:0
                cpiIncomeSpendsPercentage.setIndicatorColor(Color.parseColor(item.categoryColor))
                cvSpendsAndIncome.setOnClickListener{
                    onCategoryClick(item.categoryCode!!,item.categoryName!!)
                }
            }
        }
    }
}

object SpendAndIncomeCategoryDiffUtils:DiffUtil.ItemCallback<SpendAndIncomeCategoryUiModel>(){
    /**
     * Called to check whether two objects represent the same item.
     *
     *
     * For example, if your items have unique ids, this method should check their id equality.
     *
     *
     * Note: `null` items in the list are assumed to be the same as another `null`
     * item and are assumed to not be the same as a non-`null` item. This callback will
     * not be invoked for either of those cases.
     *
     * @param oldItem The item in the old list.
     * @param newItem The item in the new list.
     * @return True if the two items represent the same object or false if they are different.
     * @see Callback.areItemsTheSame
     */
    override fun areItemsTheSame(
        oldItem: SpendAndIncomeCategoryUiModel,
        newItem: SpendAndIncomeCategoryUiModel
    ): Boolean {
        return (oldItem.categoryCode == newItem.categoryCode)
    }

    /**
     * Called to check whether two items have the same data.
     *
     *
     * This information is used to detect if the contents of an item have changed.
     *
     *
     * This method to check equality instead of [Object.equals] so that you can
     * change its behavior depending on your UI.
     *
     *
     * For example, if you are using DiffUtil with a
     * [RecyclerView.Adapter], you should
     * return whether the items' visual representations are the same.
     *
     *
     * This method is called only if [.areItemsTheSame] returns `true` for
     * these items.
     *
     *
     * Note: Two `null` items are assumed to represent the same contents. This callback
     * will not be invoked for this case.
     *
     * @param oldItem The item in the old list.
     * @param newItem The item in the new list.
     * @return True if the contents of the items are the same or false if they are different.
     * @see Callback.areContentsTheSame
     */
    override fun areContentsTheSame(
        oldItem: SpendAndIncomeCategoryUiModel,
        newItem: SpendAndIncomeCategoryUiModel
    ): Boolean {
        return ((oldItem.categoryIcon == newItem.categoryIcon) && (oldItem.categoryName == newItem.categoryName)
                && (oldItem.amount == newItem.amount) &&
                (oldItem.percentage == newItem.percentage)&&
                (oldItem.categoryColor == newItem.categoryColor))
    }

}

@Keep
data class SpendAndIncomeCategoryUiModel(
    var categoryIcon:String?,
    var categoryName:String?,
    var amount:String?,
    var percentage:String?,
    var progress:Int?,
    var categoryColor:String?,
    var categoryCode:String?
    ){
        companion object{
            fun mapNetworkResponseToSpendAndIncomeCategoryUiModel(context: Context,categoryItem: CategoryItem):SpendAndIncomeCategoryUiModel{
                return SpendAndIncomeCategoryUiModel(
                    categoryIcon = categoryItem.iconLink,
                    categoryName = categoryItem.category,
                    amount = String.format(context.getString(R.string.amount_with_currency),categoryItem.amount),
                    percentage = categoryItem.percentage.toString()+" %",
                    progress = categoryItem.percentage?.roundToInt(),
                    categoryColor = categoryItem.categoryCol,
                    categoryCode = categoryItem.categoryCode
                )
            }
        }
    }
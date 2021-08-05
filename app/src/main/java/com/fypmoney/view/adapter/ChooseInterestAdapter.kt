package com.fypmoney.view.adapter


import android.animation.ArgbEvaluator
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.R
import com.fypmoney.base.BaseViewHolder
import com.fypmoney.bindingAdapters.loadImage
import com.fypmoney.databinding.ChooseInterestRowItemBinding
import com.fypmoney.model.InterestEntity
import com.fypmoney.viewmodel.SelectInterestViewModel
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.graphics.drawable.TransitionDrawable

import android.graphics.drawable.ColorDrawable
import com.fypmoney.util.SharedPrefUtils


/**
 * This adapter class is used to handle interest of the user
 */
class ChooseInterestAdapter(var viewModel: SelectInterestViewModel) :
    RecyclerView.Adapter<BaseViewHolder>() {
    var chooseInterestList: ArrayList<InterestEntity>? = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val mRowBinding = ChooseInterestRowItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(mRowBinding)
    }

    override fun getItemCount(): Int {
        return chooseInterestList!!.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        return holder.onBind(position)
    }

    inner class ViewHolder(
        private val mRowItemBinding: ChooseInterestRowItemBinding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        override fun onBind(position: Int) {
            mRowItemBinding!!.viewModel = viewModel
            mRowItemBinding.tvServiceName.text = chooseInterestList?.get(position)?.name
            chooseInterestList?.get(position)?.resourceId.let {
                loadImage(mRowItemBinding.ivServiceLogo,it,
                    ContextCompat.getDrawable(mRowItemBinding.ivServiceLogo.context, R.drawable.ic_sports),true)

            }
            chooseInterestList?.let {
             if(it[position].isSelected){
                 mRowItemBinding.serviceLayout.background.setColorFilter(
                     Color.parseColor(it[position].backgroundColor),
                     PorterDuff.Mode.SRC_ATOP
                 )
                 mRowItemBinding.intrestIconFl.background.setColorFilter(
                     Color.parseColor(it[position].backgroundColor),
                     PorterDuff.Mode.SRC_ATOP
                 )
                 viewModel.selectedInterestList.add(it[position])

             }else{
                 mRowItemBinding.serviceLayout.background.setColorFilter(
                     Color.WHITE,
                     PorterDuff.Mode.SRC_ATOP
                 )
                 mRowItemBinding.intrestIconFl.background.setColorFilter(
                     Color.parseColor("#f4f4f4"),
                     PorterDuff.Mode.SRC_ATOP
                 )
                 viewModel.selectedInterestList.remove(it[position])

             }
            }
            mRowItemBinding.serviceLayout.setOnClickListener {
                chooseInterestList?.get(position)?.isSelected = chooseInterestList?.get(position)?.isSelected != true
                if (chooseInterestList?.get(position)?.isSelected!!) {

                    mRowItemBinding.serviceLayout.background.setColorFilter(
                        Color.parseColor(chooseInterestList?.get(position)?.backgroundColor),
                        PorterDuff.Mode.SRC_ATOP
                    )
                    mRowItemBinding.intrestIconFl.background.setColorFilter(
                        Color.parseColor(chooseInterestList?.get(position)?.backgroundColor),
                        PorterDuff.Mode.SRC_ATOP
                    )
                    /* val color = arrayOf(ColorDrawable(Color.WHITE), ColorDrawable(Color.parseColor(chooseInterestList?.get(position)?.backgroundColor)))
                    val trans = TransitionDrawable(color)
                    mRowItemBinding.serviceLayout.background = trans
                    trans.startTransition(3000)*/
                    chooseInterestList?.get(position)?.let { viewModel.selectedInterestList.add(it) }
                } else {
                    mRowItemBinding.serviceLayout.background.setColorFilter(
                        Color.WHITE,
                        PorterDuff.Mode.SRC_ATOP
                    )
                    mRowItemBinding.intrestIconFl.background.setColorFilter(
                        Color.parseColor("#f4f4f4"),
                        PorterDuff.Mode.SRC_ATOP
                    )
                    chooseInterestList?.get(position)?.let { viewModel.selectedInterestList.remove(it) }
                }
            }
            mRowItemBinding.executePendingBindings()
        }



    }

    /**
     * This will set the data in the list in adapter
     */
    fun setList(chooseInterestList1: List<InterestEntity>?) {
        chooseInterestList!!.clear()
        chooseInterestList1!!.forEach {
            chooseInterestList!!.add(it)
        }
        notifyDataSetChanged()
    }


}
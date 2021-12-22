package com.fypmoney.view.register.adapters


import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.R
import com.fypmoney.base.BaseViewHolder
import com.fypmoney.bindingAdapters.setAnimationToViewInXAxis
import com.fypmoney.bindingAdapters.setBackgroundDrawable
import com.fypmoney.databinding.RelationCardRegisterBinding
import com.fypmoney.view.register.model.SelectRelationModel

import com.fypmoney.view.register.viewModel.SelectRelationVM


class SelectRelationAdapter(
    val chooseInterestList: ArrayList<SelectRelationModel>?,
    val homeActivityVM: SelectRelationVM
) :
    RecyclerView.Adapter<BaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val mRowBinding = RelationCardRegisterBinding.inflate(
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
        private val mRowItemBinding: RelationCardRegisterBinding? = null
    ) : BaseViewHolder(itemView = mRowItemBinding!!.root) {
        override fun onBind(position: Int) {


            // set interest types
            chooseInterestList?.get(position)?.relationImage.let {
                if (mRowItemBinding != null) {
                    mRowItemBinding.ivServiceLogo.setImageDrawable(it)

                }
            }
            mRowItemBinding?.tvServiceName?.text = chooseInterestList?.get(position)?.relationName
            // set image of interest
            mRowItemBinding?.tvServiceName?.background?.setTint(
                Color.parseColor(
                    chooseInterestList?.get(
                        position
                    )?.backgroundColor
                )
            )
            mRowItemBinding?.layoutkid?.setOnClickListener {

                chooseInterestList?.forEachIndexed { pos, item ->
                    if (pos == position) {
                        if (chooseInterestList?.get(pos)?.isSelected == 1) {
                            chooseInterestList.get(pos).isSelected = 2
                            homeActivityVM.selectedRelation.set(null)
                            // setAnimation To View in x axis positive

                        } else {
                            homeActivityVM.selectedRelation.set(chooseInterestList[pos])
                            chooseInterestList?.get(pos)?.isSelected = 1
                        }

//                           !chooseInterestList?.get(position)?.isSelected

                    } else {
                        chooseInterestList?.get(pos)?.isSelected = 0
                    }


                }
                notifyDataSetChanged()

            }
            chooseInterestList?.let { it ->
                setColorAndIconBackgroundsChange(it[position].isSelected, position)
            }

            mRowItemBinding?.executePendingBindings()
        }

        private fun setColorAndIconBackgroundsChange(selected: Int, position: Int) {
            when (selected) {
                1 -> {


                    // set interest icon background color
                    mRowItemBinding?.intrestIconFl?.setBackgroundColor(
                        Color.parseColor(chooseInterestList?.get(position)?.backgroundColor)
                    )
                    //set icon


                    // first make drawable programmatically and set background color
                    if (mRowItemBinding != null) {
                        setBackgroundDrawable(
                            mRowItemBinding.background,//view
                            Color.parseColor(chooseInterestList?.get(position)?.backgroundColor),//background color
                            30f,//radius of view
                            false// drawable type
                        )
                    }

                    // setAnimation To View in x axis positive
                    if (mRowItemBinding != null) {
                        setAnimationToViewInXAxis(
                            mRowItemBinding.background,//view
                            0f,// animation start from in x axis
                            1f,// animation end from in x axis
                            650// animation speed
                        )
                    }


                }
                0 -> {


                    mRowItemBinding?.intrestIconFl?.setBackgroundResource(R.drawable.curved_background4)

                    // setAnimation To View in x axis positive
                    mRowItemBinding?.background?.visibility = View.INVISIBLE


                }
                2 -> {
                    chooseInterestList?.get(position)?.isSelected = 0
                    if (mRowItemBinding != null) {
                        setAnimationToViewInXAxis(
                            mRowItemBinding.background,//view
                            1f,// animation start from in x axis
                            0f,// animation end from in x axis
                            450// animation speed
                        )
                    }

                }
            }
        }
    }

    /**
     * This will set the data in the list in adapter
     */


}






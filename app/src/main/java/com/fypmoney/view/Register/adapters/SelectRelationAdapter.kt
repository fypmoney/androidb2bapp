package com.fypmoney.view.Register.adapters


import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.R
import com.fypmoney.base.BaseViewHolder
import com.fypmoney.bindingAdapters.loadImage
import com.fypmoney.bindingAdapters.setAnimationToViewInXAxis
import com.fypmoney.bindingAdapters.setBackgroundDrawable
import com.fypmoney.databinding.ChooseInterestRowItemBinding
import com.fypmoney.model.InterestEntity
import com.fypmoney.viewmodel.SelectInterestViewModel


/**
 * This adapter class is used to handle interest of the user
 */
class SelectRelationAdapter(var viewModel: SelectInterestViewModel) :
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

            // set interest types
            mRowItemBinding.tvServiceName.text = chooseInterestList?.get(position)?.name
            // set image of interest
            chooseInterestList?.let { it ->
                setColorAndIconBackgroundsChange(it[position].isSelected)
            }
            mRowItemBinding.serviceLayout.setOnClickListener {
                chooseInterestList?.get(position)?.isSelected =
                    chooseInterestList?.get(position)?.isSelected != true

                setColorAndIconBackgroundsChange(chooseInterestList?.get(position)?.isSelected!!)
            }
            mRowItemBinding.executePendingBindings()
        }

        private fun setColorAndIconBackgroundsChange(selected: Boolean) {
            when (selected) {
                true -> {
                    Log.e(
                        "true",
                        "setColorAndIconBackgroundsChange:in here according of me selected ",
                    )
                    val data: InterestEntity? = chooseInterestList?.get(position)

                    // set interest icon background color
                    mRowItemBinding?.intrestIconFl?.setBackgroundColor(
                        Color.parseColor(chooseInterestList?.get(position)?.backgroundColor),
                    )
                    //set icon
                    data?.resourceIdAfterSelection.let {
                        if (mRowItemBinding != null) {
                            loadImage(
                                mRowItemBinding.ivServiceLogo, it,
                                ContextCompat.getDrawable(
                                    mRowItemBinding.ivServiceLogo.context,
                                    R.drawable.ic_sports
                                ), false
                            )
                        }
                    }

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



                    chooseInterestList?.get(position)
                        ?.let { viewModel.selectedInterestList.add(it) }
                }
                false -> {
                    Log.e(
                        "false",
                        "setColorAndIconBackgroundsChange:in here according of me Unselected ",
                    )

                    chooseInterestList?.get(position)?.resourceId.let {
                        if (mRowItemBinding != null) {
                            loadImage(
                                mRowItemBinding.ivServiceLogo, it,
                                ContextCompat.getDrawable(
                                    mRowItemBinding.ivServiceLogo.context,
                                    R.drawable.ic_sports
                                ), false
                            )
                        }
                    }
                    mRowItemBinding?.intrestIconFl?.setBackgroundResource(R.drawable.curved_background4)

                    // setAnimation To View in x axis positive
                    if (mRowItemBinding != null) {
                        setAnimationToViewInXAxis(
                            mRowItemBinding.background,//view
                            1f,// animation start from in x axis
                            0f,// animation end from in x axis
                            650// animation speed
                        )
                    }

                    viewModel.selectedInterestList.remove(chooseInterestList?.get(position))

                }
            }
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






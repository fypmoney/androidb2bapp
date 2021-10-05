package com.fypmoney.view.fragment

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentRewardsOverviewBinding
import com.fypmoney.databinding.FragmentSpinnerListBinding
import com.fypmoney.model.aRewardProductResponse
import com.fypmoney.util.AppConstants
import com.fypmoney.view.activity.ScratchCardActivity
import com.fypmoney.view.activity.SpinWheelViewDark
import com.fypmoney.view.adapter.ScratchItemAdapter
import com.fypmoney.view.adapter.SpinnerAdapter
import com.fypmoney.view.interfaces.ListContactClickListener

import com.fypmoney.viewmodel.RewardsViewModel
import kotlinx.android.synthetic.main.dialog_burn_mynts.*
import kotlinx.android.synthetic.main.fragment_spinner_list.view.*


import kotlin.collections.ArrayList


class RewardsSpinnerListFragment : BaseFragment<FragmentSpinnerListBinding, RewardsViewModel>() {
    companion object {
        var page = 0

    }

    private var sharedViewModel: RewardsViewModel? = null
    private var dialogDialog: Dialog? = null
    private var itemsArrayList: ArrayList<aRewardProductResponse> = ArrayList()

    private var typeAdapter: SpinnerAdapter? = null
    private var scratchAdapter: ScratchItemAdapter? = null
    private var mViewBinding: FragmentSpinnerListBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()

        setRecyclerView()
        setRvScratchCard()
        dialogDialog = Dialog(requireContext())


    }


    override fun onTryAgainClicked() {
        TODO("Not yet implemented")
    }

    private fun observeInput(sharedViewModel: RewardsViewModel) {

        sharedViewModel.spinArrayList.observe(
            requireActivity(),
            androidx.lifecycle.Observer { list ->

                itemsArrayList.clear()

                itemsArrayList.addAll(list)
                typeAdapter?.notifyDataSetChanged()

            })

        sharedViewModel.coinsBurned.observe(
            requireActivity(),
            androidx.lifecycle.Observer { list ->
                dialogDialog?.dismiss()
                when (sharedViewModel.clickedType.get()) {
                    AppConstants.PRODUCT_SPIN -> {
                        val intent = Intent(requireContext(), SpinWheelViewDark::class.java)
                        SpinWheelViewDark.sectionArrayList.clear()

                        itemsArrayList[sharedViewModel.selectedPosition.get()!!].sectionList?.forEachIndexed { pos, item ->
                            if (item != null) {
                                SpinWheelViewDark.sectionArrayList.add(item)
                            }
                        }


//                    val args = Bundle()
//                    args.putSerializable("ARRAYLIST", itemsArrayList as Serializable)
//                    intent.putExtra("BUNDLE", args)
                        requireContext().startActivity(intent)

                    }
                    AppConstants.PRODUCT_SCRATCH -> {
                        val intent = Intent(requireContext(), ScratchCardActivity::class.java)
                        requireContext().startActivity(intent)


                    }
                }

            })


    }

    internal fun showBurnDialog(i: Int, type: String) {


        dialogDialog!!.setCancelable(true)
        dialogDialog!!.setCanceledOnTouchOutside(true)
        dialogDialog!!.setContentView(R.layout.dialog_burn_mynts)

        val wlp = dialogDialog!!.window!!.attributes

        wlp.width = ViewGroup.LayoutParams.MATCH_PARENT

        dialogDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogDialog!!.window!!.attributes = wlp



        dialogDialog!!.clicked.setOnClickListener(View.OnClickListener {
            when (type) {
                AppConstants.PRODUCT_SPIN -> {
                    sharedViewModel?.selectedPosition?.set(i)
                    sharedViewModel?.clickedType?.set(AppConstants.PRODUCT_SPIN)
                    sharedViewModel?.callRewardsRedeem(itemsArrayList[i].code)


                }
                AppConstants.PRODUCT_SCRATCH -> {
                    sharedViewModel?.clickedType?.set(AppConstants.PRODUCT_SCRATCH)
                    sharedViewModel?.selectedPosition?.set(i)

                    sharedViewModel?.callRewardsRedeem(itemsArrayList[i].code)


                }
            }

        })


        dialogDialog!!.show()
    }


    private fun setRecyclerView() {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mViewBinding?.rvSpinner?.layoutManager = layoutManager


        var itemClickListener2 = object : ListContactClickListener {


            override fun onItemClicked(pos: Int) {
                showBurnDialog(pos, AppConstants.PRODUCT_SPIN)


            }
        }

        typeAdapter = SpinnerAdapter(itemsArrayList, requireContext(), itemClickListener2!!)
        mViewBinding?.rvSpinner?.adapter = typeAdapter
    }

    private fun setRvScratchCard() {
        val layoutManager =
            GridLayoutManager(requireContext(), 2)
        mViewBinding?.rvScratch?.layoutManager = layoutManager


        var itemClickListener2 = object : ListContactClickListener {
            override fun onItemClicked(pos: Int) {
                showBurnDialog(pos, AppConstants.PRODUCT_SCRATCH)
            }
        }

        scratchAdapter = ScratchItemAdapter(itemsArrayList, requireContext(), itemClickListener2!!)
        mViewBinding?.rvScratch!!.adapter = scratchAdapter
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_spinner_list
    }

    override fun getViewModel(): RewardsViewModel {
        activity?.let {
            sharedViewModel = ViewModelProvider(it).get(RewardsViewModel::class.java)
            observeInput(sharedViewModel!!)

        }
        return sharedViewModel!!
    }


}
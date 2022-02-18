package com.fypmoney.view.recharge.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentForYouPlansBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.view.interfaces.ListContactClickListener
import com.fypmoney.view.rewardsAndWinnings.viewModel.SpinnerFragmentVM
import kotlinx.android.synthetic.main.toolbar.*


class RechargeForYouFragment : BaseFragment<FragmentForYouPlansBinding, SpinnerFragmentVM>() {
    companion object {

    }

    private lateinit var mViewBinding: FragmentForYouPlansBinding
    private var mViewmodel: SpinnerFragmentVM? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_for_you_plans
    }

    override fun getViewModel(): SpinnerFragmentVM {

        mViewmodel = ViewModelProvider(this).get(SpinnerFragmentVM::class.java)



        return mViewmodel!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()


        setRecyclerView()


    }


    override fun onTryAgainClicked() {

    }


    private fun setRecyclerView() {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mViewBinding?.rvPlans?.layoutManager = layoutManager


        val itemClickListener2 = object : ListContactClickListener {


            override fun onItemClicked(pos: Int) {


            }
        }

//        spinnerAdapter = SpinnerAdapter(itemsArrayList, requireContext(), itemClickListener2!!)
//        mViewBinding?.rvSpinner?.adapter = spinnerAdapter
//        if (itemsArrayList.size > 0) {
//
//            mViewBinding?.shimmerLayout?.visibility = View.GONE
//            mViewBinding?.shimmerLayout?.stopShimmer()
//        }

    }


}
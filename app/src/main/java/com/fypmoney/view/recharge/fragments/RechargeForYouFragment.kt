package com.fypmoney.view.recharge.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentForYouPlansBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.view.recharge.adapter.RechargePlansAdapter
import com.fypmoney.view.recharge.model.ValueItem
import com.fypmoney.view.recharge.viewmodel.RechargeForYouFragmentVM


class RechargeForYouFragment(val list: List<ValueItem?>?, val click: (ValueItem) -> Unit) :
    BaseFragment<FragmentForYouPlansBinding, RechargeForYouFragmentVM>() {

    private val rechargeForYouFragmentVM by viewModels<RechargeForYouFragmentVM> { defaultViewModelProviderFactory }

    private lateinit var mViewBinding: FragmentForYouPlansBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_for_you_plans
    }

    override fun getViewModel(): RechargeForYouFragmentVM  = rechargeForYouFragmentVM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()
        setUpRecyclerView()
        updatePlanList(list)

    }


    override fun onTryAgainClicked() {

    }


    private fun setUpRecyclerView() {
        val planAdapter = RechargePlansAdapter(
            this, onPlanClicked = {
                click(it)
            })
        with(mViewBinding.rvPlans) {
            adapter = planAdapter
            layoutManager =
                LinearLayoutManager(
                    requireContext(),
                    androidx.recyclerview.widget.RecyclerView.VERTICAL,
                    false
                )
        }



    }

    fun updatePlanList(plansList: List<ValueItem?>?){
        if (plansList?.isNotEmpty() == true) {
            mViewBinding.shimmerPlans.visibility = View.GONE
            mViewBinding.shimmerPlans.stopShimmer()
            (mViewBinding.rvPlans.adapter as RechargePlansAdapter).submitList(plansList)

        }else{
            mViewBinding.shimmerPlans.visibility = View.GONE
            mViewBinding.shimmerPlans.stopShimmer()
            mViewBinding.rvPlans.toGone()
            mViewBinding.noRecharageFoundTv.toVisible()
        }
    }


}
package com.fypmoney.view.recharge


import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.viewpager.widget.ViewPager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment

import com.fypmoney.databinding.ViewPlanSelectionBinding
import com.fypmoney.view.recharge.fragments.RechargeForYouFragment
import com.fypmoney.view.recharge.model.RechargePlansResponse
import com.fypmoney.view.recharge.viewmodel.PlansViewModel
import com.fypmoney.view.rewardsAndWinnings.RewardsActivity
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.toolbar.toolbar

/*
* This class is used as Home Screen
* */
class PlanSelectionActivity : BaseFragment<ViewPlanSelectionBinding, PlansViewModel>() {
    private lateinit var mViewModel: PlansViewModel
    private lateinit var mViewBinding: ViewPlanSelectionBinding
    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager

    private val args: PlanSelectionActivityArgs by navArgs()
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_plan_selection
    }

    override fun getViewModel(): PlansViewModel {
        mViewModel = ViewModelProvider(this).get(PlansViewModel::class.java)
        return mViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()
        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar, backArrowTint = Color.WHITE,
            titleColor = Color.WHITE,

            isBackArrowVisible = true, toolbarTitle = "Airtel"
        )

        mViewModel.selectedOperator.value = args.selectedOperator
        mViewModel.selectedCircle.value = args.selectedCircle
//        loadProfile(
//            SharedPrefUtils.getString(
//                applicationContext,
//                SharedPrefUtils.SF_KEY_PROFILE_IMAGE
//            )
//        )
        tabLayout = mViewBinding.tabLayout
        viewPager = mViewBinding.viewPager
        setObserver()

        mViewModel.callGetOperatorList()

    }

    private fun setObserver() {
        mViewModel.opertaorList.observe(viewLifecycleOwner) {
            initializeTabs(tabLayout, it)
        }
    }

    override fun onTryAgainClicked() {

    }


    private fun initializeTabs(
        tabLayout: TabLayout,
        rechargePlansResponse: ArrayList<RechargePlansResponse>
    ) {


        val adapter = RewardsActivity.ViewPagerAdapter(childFragmentManager)

        rechargePlansResponse.forEach {
            it.name?.let { it1 -> adapter.addFragment(RechargeForYouFragment(it.value), it1) }
        }



        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)

//        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab) {
//
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab) {
//
//            }
//
//            override fun onTabReselected(tab: TabLayout.Tab) {}
//        })


    }


}

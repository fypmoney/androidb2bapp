package com.fypmoney.view.recharge


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.base.BaseFragment

import com.fypmoney.databinding.ViewPlanSelectionBinding
import com.fypmoney.view.recharge.fragments.RechargeForYouFragment
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()
        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar, backArrowTint = Color.WHITE,
            titleColor = Color.WHITE,

            isBackArrowVisible = true, toolbarTitle = "Airtel"
        )

//        loadProfile(
//            SharedPrefUtils.getString(
//                applicationContext,
//                SharedPrefUtils.SF_KEY_PROFILE_IMAGE
//            )
//        )
        tabLayout = mViewBinding.tabLayout
        viewPager = mViewBinding.viewPager

        initializeTabs(tabLayout)

    }

    override fun onTryAgainClicked() {

    }


    private fun initializeTabs(tabLayout: TabLayout) {


        val adapter = RewardsActivity.ViewPagerAdapter(childFragmentManager)


        adapter.addFragment(RechargeForYouFragment(), "For you")
        adapter.addFragment(RechargeForYouFragment(), "Recommended")
        adapter.addFragment(RechargeForYouFragment(), "Unlimited")
        adapter.addFragment(RechargeForYouFragment(), "data")
        adapter.addFragment(RechargeForYouFragment(), "sms")

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

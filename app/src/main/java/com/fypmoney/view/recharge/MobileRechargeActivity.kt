package com.fypmoney.view.recharge


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity

import com.fypmoney.databinding.ActivityMobileRechargeBinding
import com.fypmoney.view.recharge.fragments.RechargeForYouFragment
import com.fypmoney.view.rewardsAndWinnings.RewardsActivity
import com.fypmoney.viewmodel.UserProfileViewModel
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.toolbar.toolbar

/*
* This class is used as Home Screen
* */
class MobileRechargeActivity : BaseActivity<ActivityMobileRechargeBinding, UserProfileViewModel>() {
    private lateinit var mViewModel: UserProfileViewModel
    private lateinit var mViewBinding: ActivityMobileRechargeBinding
    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_mobile_recharge
    }

    override fun getViewModel(): UserProfileViewModel {
        mViewModel = ViewModelProvider(this).get(UserProfileViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()
        setToolbarAndTitle(
            context = this@MobileRechargeActivity,
            toolbar = toolbar, backArrowTint = Color.WHITE,
            titleColor = Color.WHITE,

            isBackArrowVisible = true, toolbarTitle = "Mobile Recharge"
        )


    }


}

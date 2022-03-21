package com.fypmoney.view.recharge


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.base.BaseFragment

import com.fypmoney.databinding.ActivityMobileRechargeBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.view.interfaces.ListContactClickListener
import com.fypmoney.view.recharge.adapter.recentRechargeAdapter
import com.fypmoney.view.recharge.fragments.RechargeForYouFragment
import com.fypmoney.view.rewardsAndWinnings.RewardsActivity
import com.fypmoney.viewmodel.UserProfileViewModel
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.toolbar.toolbar

/*
* This class is used as Home Screen
* */
class MobileRechargeActivity : BaseFragment<ActivityMobileRechargeBinding, UserProfileViewModel>() {
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
            context = requireContext(),
            toolbar = toolbar, backArrowTint = Color.WHITE,
            titleColor = Color.WHITE,

            isBackArrowVisible = true, toolbarTitle = "Mobile Recharge"
        )
        setRecyclerView()
        setListners()
    }

    override fun onTryAgainClicked() {

    }

    private fun setListners() {
        mViewBinding.continueBtn.setOnClickListener {

            var intent = Intent(requireContext(), SelectCircleActivity::class.java)
            intent.putExtra(AppConstants.NUMBER_SELECTED, mViewBinding.etNumber.text.toString())

            startActivity(intent)


        }
        mViewBinding.etNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mViewBinding.continueBtn.isEnabled = !s.isNullOrEmpty() && s.length <= 10
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    private fun setRecyclerView() {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mViewBinding?.rvRecents?.layoutManager = layoutManager


        var recentrechargeAdapter = recentRechargeAdapter()
        mViewBinding?.rvRecents?.adapter = recentrechargeAdapter


    }


}

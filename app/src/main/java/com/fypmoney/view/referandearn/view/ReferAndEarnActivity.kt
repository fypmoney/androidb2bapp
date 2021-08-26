package com.fypmoney.view.referandearn.view

import android.content.ClipboardManager
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivityReferAndEarnBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.referandearn.viewmodel.ReferAndEarnActivityVM
import kotlinx.android.synthetic.main.toolbar.*

class ReferAndEarnActivity : BaseActivity<ActivityReferAndEarnBinding,ReferAndEarnActivityVM>() {
    private lateinit var mViewModel: ReferAndEarnActivityVM
    private lateinit var mViewBinding: ActivityReferAndEarnBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()
        mViewBinding.viewmodel = mViewModel
        setToolbarAndTitle(
            context = this@ReferAndEarnActivity,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.refer_and_earn)
        )
        setUpViews()
        setUpObserver()
    }

    private fun setUpViews() {
        mViewBinding.referalCodeValueTv.text =  Utility.getCustomerDataFromPreference()?.referralCode
        mViewBinding.referAndEarnTitleTv.text =  getString(R.string.refer_to_your_friend_and_get_a_cash_reward_of_50,
            AppConstants.CASHBACK_AMOUNT
        )
        mViewBinding.referAndEarnSubTitleTv.text =  getString(R.string.share_referral_code_with_your_friend_and_after_they_install_both_of_you_will_get_50_cash_rewards,
            AppConstants.CASHBACK_AMOUNT
        )
    }

    private fun setUpObserver() {
        mViewModel.event.observe(this, {
            handelEvents(it)
        })
        mViewModel.state.observe(this,{
            when(it){
                ReferAndEarnActivityVM.ReferAndEarnState.Error -> {
                    mViewBinding.totalRefralWonValueTv.text =  "N/A"

                }
                ReferAndEarnActivityVM.ReferAndEarnState.Loading -> {
                    mViewBinding.loadingAmountHdp.visibility = View.VISIBLE
                }
                is ReferAndEarnActivityVM.ReferAndEarnState.PopulateData -> {
                    mViewBinding.loadingAmountHdp.clearAnimation()
                    mViewBinding.loadingAmountHdp.visibility = View.GONE
                    mViewBinding.totalRefralWonValueTv.text =  Utility.convertToRs("${it.totalAmount}")

                }
            }
        })
    }

    private fun handelEvents(it: ReferAndEarnActivityVM.ReferAndEarnEvent?) {
        when(it){
            ReferAndEarnActivityVM.ReferAndEarnEvent.CopyReferCode -> {
                Utility.copyTextToClipBoard(
                    getSystemService(CLIPBOARD_SERVICE) as ClipboardManager,
                    Utility.getCustomerDataFromPreference()?.referralCode
                )
            }
            ReferAndEarnActivityVM.ReferAndEarnEvent.ShareReferCode -> {
                inviteUser()
            }
        }
    }

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int  = R.layout.activity_refer_and_earn

    override fun getViewModel(): ReferAndEarnActivityVM {
        mViewModel = ViewModelProvider(this).get(ReferAndEarnActivityVM::class.java)
        return mViewModel
    }

}
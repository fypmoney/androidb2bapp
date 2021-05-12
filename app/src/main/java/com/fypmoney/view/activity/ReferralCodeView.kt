package com.fypmoney.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewLoginSuccessBinding
import com.fypmoney.databinding.ViewReferralCodeBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.viewmodel.ReferralCodeViewModel

/*
* This class is used for show referral code
* */
class ReferralCodeView : BaseActivity<ViewReferralCodeBinding, ReferralCodeViewModel>() {
    private lateinit var mViewModel: ReferralCodeViewModel

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_referral_code
    }

    override fun getViewModel(): ReferralCodeViewModel {
        mViewModel = ViewModelProvider(this).get(ReferralCodeViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setObserver()
    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        mViewModel.onSkipClicked.observe(this) {
            if (it) {
                intentToActivity()
                mViewModel.onSkipClicked.value = false
            }

        }

    }


    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity() {
        when (intent.getStringExtra(AppConstants.IS_PROFILE_COMPLETED)) {
            AppConstants.NO -> {
                startActivity(Intent(this@ReferralCodeView, CreateAccountView::class.java))
                finish()

            }
            else -> {
                startActivity(Intent(this@ReferralCodeView, HomeView::class.java))
                finish()
            }
        }
    }

}

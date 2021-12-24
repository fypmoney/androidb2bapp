package com.fypmoney.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewReferralCodeBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.register.PanAdhaarSelectionActivity
import com.fypmoney.view.register.UserTypeOnLoginView
import com.fypmoney.viewmodel.ReferralCodeViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar_animation.*

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
        setLottieAnimationToolBar(
            isBackArrowVisible = false,//back arrow visibility
            isLottieAnimation = true,// lottie animation visibility
            imageView = ivToolBarBack,//back image view
            lottieAnimationView = ivAnimationGift,
            screenName = ReferralCodeView::class.java.simpleName

        )// lottie anima
        setObserver()
    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        mViewModel.onSkipClicked.observe(this) {
            if (it) {
                mViewModel.callGetCustomerProfileApi()
                mViewModel.onSkipClicked.value = false
            }

        }
        mViewModel.onReferralCodeSuccess.observe(this) {
            if (it) {

                mViewModel.callGetCustomerProfileApi()
                mViewModel.onReferralCodeSuccess.value = false
            }

        }

        mViewModel.onCustomerApiSuccess.observe(this) {
            if (it) {
                intentToActivity()
                mViewModel.onCustomerApiSuccess.value = false
            }

        }


    }


    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity() {
        when {
            (intent.getStringExtra(AppConstants.IS_PROFILE_COMPLETED) == AppConstants.NO) or
                    (Utility.getCustomerDataFromPreference()?.isProfileCompleted==AppConstants.NO)-> {
                startActivity(Intent(this@ReferralCodeView, UserTypeOnLoginView::class.java))
                finishAffinity()

            }
            Utility.getCustomerDataFromPreference()!!.bankProfile?.isAccountActive == AppConstants.NO -> {
                startActivity(
                    Intent(
                        this@ReferralCodeView,
                        PanAdhaarSelectionActivity::class.java
                    )
                )
                finishAffinity()
            }
            else -> {
                startActivity(Intent(this@ReferralCodeView, ChooseInterestRegisterView::class.java))

                finishAffinity()

            }
        }
    }

}

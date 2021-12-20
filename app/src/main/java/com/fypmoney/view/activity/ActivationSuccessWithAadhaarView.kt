package com.fypmoney.view.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustEvent
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.UserTrackr
import com.fyp.trackr.models.push
import com.fyp.trackr.models.trackr
import com.fyp.trackr.services.TrackrServices
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewActivationSuccessWithAadhaarBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.register.InviteParentSiblingActivity
import com.fypmoney.viewmodel.ActivationSuccessWithAadhaarViewModel
import com.moengage.core.internal.MoEConstants
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar_animation.*

/*
* This class is used to show success message on account activation with Aadhaar
* */
class ActivationSuccessWithAadhaarView : BaseActivity<ViewActivationSuccessWithAadhaarBinding, ActivationSuccessWithAadhaarViewModel>() {
    private lateinit var mViewModel: ActivationSuccessWithAadhaarViewModel

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_activation_success_with_aadhaar
    }

    override fun getViewModel(): ActivationSuccessWithAadhaarViewModel {
        mViewModel = ViewModelProvider(this).get(ActivationSuccessWithAadhaarViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLottieAnimationToolBar(
            isBackArrowVisible = false,//back arrow visibility
            isLottieAnimation = true,// lottie animation visibility
            imageView = ivToolBarBack,//back image view
            lottieAnimationView = ivAnimationGift
        )

        var postkyc = intent?.getStringExtra(AppConstants.POSTKYCKEY)
        mViewModel.postKycScreenCode.value = postkyc



        setObserver()

    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {

        mViewModel.GiftsSuccess.observe(this)
        {
            if (it) {
                if (mViewModel.postKycScreenCode.value != null && mViewModel.postKycScreenCode.value == "1") {

                    val intent = Intent(this, InviteParentSiblingActivity::class.java)
                    intent.putExtra(AppConstants.USER_TYPE, "1")
                    startActivity(intent)
                    finish()

                } else if (mViewModel.postKycScreenCode.value != null && mViewModel.postKycScreenCode.value == "0") {
                    when (Utility.getCustomerDataFromPreference()?.isReferralAllowed) {
                        AppConstants.YES -> {
                            intentToActivity(ReferralCodeView::class.java)
                        }
                        else -> {
                            startActivity(Intent(this, ChooseInterestRegisterView::class.java))

                        }
                    }
                } else if (mViewModel.postKycScreenCode.value != null && mViewModel.postKycScreenCode.value == "90") {
                    val intent = Intent(this, InviteParentSiblingActivity::class.java)
                    intent.putExtra(AppConstants.USER_TYPE, "90")
                    startActivity(intent)
                    finish()
                }
                mViewModel.GiftsSuccess.value = false
            }

        }
        mViewModel.onContinueClicked.observe(this)
        {
            if (it) {
                mViewModel.callUserGiftsApi()



                mViewModel.onContinueClicked.value = false
            }

        }
    }

    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(aClass: Class<*>) {
        startActivity(Intent(this@ActivationSuccessWithAadhaarView, aClass))
        finishAffinity()
    }

}
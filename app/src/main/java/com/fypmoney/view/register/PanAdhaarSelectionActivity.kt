package com.fypmoney.view.register

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.bindingAdapters.setBackgroundDrawable
import com.fypmoney.databinding.ActivityKycTypeBinding

import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.register.viewModel.KycTypeVM

import com.fypmoney.view.activity.CreateAccountView
import com.fypmoney.view.activity.EnterOtpView


class PanAdhaarSelectionActivity :
    BaseActivity<ActivityKycTypeBinding, KycTypeVM>() {
    private lateinit var mVM: KycTypeVM
    private lateinit var mViewBinding: ActivityKycTypeBinding


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_kyc_type
    }

    override fun getViewModel(): KycTypeVM {
        mVM = ViewModelProvider(this).get(KycTypeVM::class.java)
        return mVM
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()

        setObserver()
    }

    private fun setObserver() {
        var userTypeSelected: String = "NOTSELECTED"
        mVM.isPanClick.observe(this) {
            setSelectedUserType(getString(R.string.pan))
            userTypeSelected = getString(R.string.pan)

        }
        mVM.isAadhaarClicked.observe(this) {
            setSelectedUserType(getString(R.string.aadhaar))
            userTypeSelected = getString(R.string.aadhaar)

        }
        mVM.isContinueClick.observe(this) {
            if (userTypeSelected == "NOTSELECTED") {
                Utility.showToast("Select any one")

            } else {
                val intent = Intent(this, KycDetailsActivity::class.java)
                intent.putExtra(AppConstants.KYC_type, userTypeSelected)
                startActivity(intent)

            }
        }
    }

    private fun setSelectedUserType(type: String) {
        if (type == getString(R.string.pan)) {
            setBackgroundDrawable(
                mViewBinding.cvAadhaar,
                resources.getColor(R.color.white),
                38f,
                resources.getColor(R.color.white),
                0f,
                false
            )

            setBackgroundDrawable(
                mViewBinding.cvPan,
                resources.getColor(R.color.green_selected_light),
                38f,
                resources.getColor(R.color.green_selected),
                4f,
                false
            )


        } else {
            setBackgroundDrawable(
                mViewBinding.cvAadhaar,
                resources.getColor(R.color.green_selected_light),
                38f,
                resources.getColor(R.color.green_selected),
                4f,
                false
            )
            setBackgroundDrawable(
                mViewBinding.cvPan,
                resources.getColor(R.color.white),
                38f,
                resources.getColor(R.color.white),
                0f,
                false
            )


        }

    }

    private fun goToEnterOtpScreen(token: String) {
        val intent = Intent(this, EnterOtpView::class.java)
        intent.putExtra(
            AppConstants.MOBILE_TYPE,
            ""
        )
        intent.putExtra(
            AppConstants.FROM_WHICH_SCREEN, AppConstants.AADHAAR_VERIFICATION
        )

        intent.putExtra(
            AppConstants.MOBILE_WITHOUT_COUNTRY_CODE,
            ""
        )

        intent.putExtra(
            AppConstants.KYC_ACTIVATION_TOKEN, token

        )
        intent.putExtra(
            AppConstants.KIT_FOUR_DIGIT, ""

        )
        startActivity(intent)
    }
}

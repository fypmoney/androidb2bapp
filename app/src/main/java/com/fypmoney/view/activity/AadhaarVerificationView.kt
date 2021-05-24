package com.fypmoney.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewAadhaarVerificationBinding
import com.fypmoney.databinding.ViewCommunityBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.viewmodel.AadhaarVerificationViewModel
import kotlinx.android.synthetic.main.toolbar.*

/*
* This class is used to handle account activation via aadhaar card
* */
class AadhaarVerificationView :
    BaseActivity<ViewAadhaarVerificationBinding, AadhaarVerificationViewModel>() {
    private lateinit var mViewModel: AadhaarVerificationViewModel

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_aadhaar_verification
    }

    override fun getViewModel(): AadhaarVerificationViewModel {
        mViewModel = ViewModelProvider(this).get(AadhaarVerificationViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarAndTitle(
            context = this@AadhaarVerificationView,
            toolbar = toolbar,
            isBackArrowVisible = true
        )
        setObserver()
    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        mViewModel.onGetOtpClicked.observe(this) {
            goToEnterOtpScreen()
        }


    }


    /**
     * Method to navigate to the Feeds screen after login
     */
    private fun goToEnterOtpScreen() {
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
        startActivity(intent)
    }

}

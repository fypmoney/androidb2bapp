package com.fypmoney.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewAadhaarVerificationBinding
import com.fypmoney.databinding.ViewCommunityBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SeparatorTextWatcher
import com.fypmoney.viewmodel.AadhaarVerificationViewModel
import kotlinx.android.synthetic.main.bottom_sheet_block_unblock_card.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.toolbar
import kotlinx.android.synthetic.main.toolbar_for_aadhaar.*
import kotlinx.android.synthetic.main.view_aadhaar_verification.*
import java.lang.Exception

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
        helpValue.setOnClickListener {
            callFreshChat(applicationContext)

        }

        setObserver()

        et_first_name.addTextChangedListener(object : SeparatorTextWatcher(' ', 4) {
            override fun onAfterTextChanged(text: String) {
                et_first_name.run {
                    setText(text)
                    setSelection(text.length)
                }
            }
        })

    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        mViewModel.onKycInitSuccess.observe(this) {
            goToEnterOtpScreen(token = it.token)
        }


    }


    /**
     * Method to navigate to the Feeds screen after login
     */
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

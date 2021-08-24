package com.fypmoney.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewAadhaarAccountActivationBinding
import com.fypmoney.databinding.ViewAadhaarVerificationBinding
import com.fypmoney.databinding.ViewCommunityBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.viewmodel.AadhaarAccountActivationViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.toolbar
import kotlinx.android.synthetic.main.toolbar_for_aadhaar.*
import kotlinx.android.synthetic.main.view_aadhaar_account_activation.*
import kotlinx.android.synthetic.main.view_login.*

/*
* This class is used to handle school name city
* */
class AadhaarAccountActivationView :
    BaseActivity<ViewAadhaarAccountActivationBinding, AadhaarAccountActivationViewModel>() {
    private lateinit var mViewModel: AadhaarAccountActivationViewModel

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_aadhaar_account_activation
    }

    override fun getViewModel(): AadhaarAccountActivationViewModel {
        mViewModel = ViewModelProvider(this).get(AadhaarAccountActivationViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarAndTitle(
            context = this@AadhaarAccountActivationView,
            toolbar = toolbar,
            isBackArrowVisible = true
        )
        helpValue.setOnClickListener {
            callFreshChat(applicationContext)

        }
        //Test Commit


        val ss = SpannableString(getString(R.string.account_verification_sub_title))
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ContextCompat.getColor(applicationContext, R.color.text_color_dark)
            }
        }
        ss.setSpan(clickableSpan, 49, 53, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvSubTitle.text = ss
        tvSubTitle.movementMethod = LinkMovementMethod.getInstance()


        setObserver()
    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        mViewModel.onActivateAccountSuccess.observe(this) {
            if (it.showMobileOtpVerificationScreen == AppConstants.YES)
                intentToActivity(it.token)
            else {
                intentToHomeActivity(HomeView::class.java)
            }
        }


    }


    /*
   * navigate to the different screen
   * */
    private fun intentToActivity(token: String) {
        val intent = Intent(this, EnterOtpView::class.java)
        intent.putExtra(
            AppConstants.MOBILE_TYPE,
            ""
        )
        intent.putExtra(
            AppConstants.FROM_WHICH_SCREEN, AppConstants.KYC_MOBILE_VERIFICATION
        )

        intent.putExtra(
            AppConstants.MOBILE_WITHOUT_COUNTRY_CODE,
            ""
        )

        intent.putExtra(
            AppConstants.KYC_ACTIVATION_TOKEN,
            token
        )
        intent.putExtra(
            AppConstants.KIT_FOUR_DIGIT, ""

        )
        startActivity(intent)
    }

    /**
     * Method to navigate to the different activity
     */
    private fun intentToHomeActivity(aClass: Class<*>) {
        startActivity(Intent(this@AadhaarAccountActivationView, aClass))
        finishAffinity()
    }


}

package com.fypmoney.view.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewAadhaarVerificationBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SeparatorTextWatcher
import com.fypmoney.viewmodel.AadhaarVerificationViewModel
import kotlinx.android.synthetic.main.bottom_sheet_block_unblock_card.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.toolbar
import kotlinx.android.synthetic.main.toolbar_for_aadhaar.*
import kotlinx.android.synthetic.main.view_aadhaar_verification.*
import kotlinx.android.synthetic.main.view_enter_otp.*

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
        //helpValue.visibility = View.INVISIBLE
        setObserver()

        et_first_name.addTextChangedListener(object : SeparatorTextWatcher(' ', 4) {
            override fun onAfterTextChanged(text: String) {
                et_first_name.run {
                    setText(text)
                    setSelection(text.length)
                }
            }
        })

        mViewModel.aadhaarNumber.observe(this,{
            val text =it?.filter { !it.isWhitespace() }
            if(text?.length!! < 12){
                btnGetOtp.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        this,
                        R.color.buttonUnselectedColor
                    ))
                btnGetOtp.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.text_color_little_dark
                    )
                )
                btnGetOtp.isEnabled = false
            }else{
                btnGetOtp.isEnabled = true

                btnGetOtp.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
                    this,
                    R.color.black
                ))
                btnGetOtp.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.white
                    )
                )
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
        mViewModel.kycUpgraded.observe(this) {
            when(intent.getStringExtra(AppConstants.KYC_UPGRADE_FROM_WHICH_SCREEN)){
                AddMoneyView::class.java.simpleName->{
                    startActivity(Intent(this@AadhaarVerificationView,AddMoneyView::class.java))
                }
                PayRequestProfileView::class.java.simpleName->{
                    startActivity(Intent(this@AadhaarVerificationView,PayRequestProfileView::class.java))

                }
                UserProfileView::class.java.simpleName->{
                    startActivity(Intent(this@AadhaarVerificationView,UserProfileView::class.java))

                }
            }
        }
        mViewModel.clickHere.observe(this) {
            if(it){
                callFreshChat(applicationContext)
                mViewModel.clickHere.value = false
            }
        }
        mViewModel.onVerificationFailed.observe(this) {
            when(it){
                AppConstants.API_FAIL->{
                    finish()
                }
            }


        }


    }


    /**
     * Method to navigate to the Feeds screen after login
     */
    private fun goToEnterOtpScreen(token: String) {
        val intent1 = Intent(this, EnterOtpView::class.java)
        intent1.putExtra(
            AppConstants.MOBILE_TYPE,
            ""
        )
        intent1.putExtra(
            AppConstants.FROM_WHICH_SCREEN, AppConstants.AADHAAR_VERIFICATION
        )

        intent1.putExtra(
            AppConstants.MOBILE_WITHOUT_COUNTRY_CODE,
            ""
        )

        intent1.putExtra(
            AppConstants.KYC_ACTIVATION_TOKEN, token

        )
        intent1.putExtra(
            AppConstants.KIT_FOUR_DIGIT, ""

        )
        intent1.putExtra(AppConstants.KYC_UPGRADE_FROM_WHICH_SCREEN,
            intent.getStringExtra(AppConstants.KYC_UPGRADE_FROM_WHICH_SCREEN))

        startActivity(intent1)
    }

}

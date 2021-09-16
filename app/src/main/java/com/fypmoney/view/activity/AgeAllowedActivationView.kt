package com.fypmoney.view.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import com.fypmoney.base.BaseViewModel
import com.fypmoney.databinding.ViewAgeAllowedAccountBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.view.fragment.LogoutBottomSheet
import com.fypmoney.viewmodel.AadhaarAccountActivationViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.toolbar
import kotlinx.android.synthetic.main.toolbar_for_aadhaar.*
import kotlinx.android.synthetic.main.view_aadhaar_account_activation.*
import kotlinx.android.synthetic.main.view_login.*

/*
* This class is used to handle school name city
* */
class AgeAllowedActivationView :
    BaseActivity<ViewAgeAllowedAccountBinding, BaseViewModel>() {
    private lateinit var mViewModel: BaseViewModel

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_age_allowed_account
    }

    override fun getViewModel(): BaseViewModel {
        mViewModel = ViewModelProvider(this).get(AadhaarAccountActivationViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setToolbarAndTitle(
//            context = this@AgeAllowedActivationView,
//            toolbar = toolbar,
//            isBackArrowVisible = false
//        )


    }


}

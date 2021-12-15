package com.fypmoney.view.activity

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.util.AppConstants
import com.fypmoney.view.fragment.*

import android.content.res.ColorStateList
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import com.fypmoney.util.textview.ClickableSpanListener
import com.fypmoney.util.textview.MyStoreClickableSpan
import com.fypmoney.view.webview.ARG_WEB_PAGE_TITLE
import com.fypmoney.view.webview.ARG_WEB_URL_TO_OPEN
import com.fypmoney.view.webview.WebViewActivity
import com.fypmoney.viewmodel.PermissionViewModel
import kotlinx.android.synthetic.main.toolbar.*

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.fypmoney.databinding.ViewPermissionActivityBinding

import com.fypmoney.util.Utility
import kotlinx.android.synthetic.main.bottom_sheet_redeem_coins.view.*
import kotlinx.android.synthetic.main.view_permission_activity.*


/*
* This is used to handle chores
* */



class PermissionsActivity : BaseActivity<ViewPermissionActivityBinding, PermissionViewModel>() {
    private lateinit var mViewModel: PermissionViewModel
    var PERMISSIONS = arrayOf(
        Manifest.permission.READ_CONTACTS
    )
    var PERMISSIONS2 = arrayOf(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION

    )
    var PERMSSIONS2 = 31
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permission2_cb.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!isChecked) {
                btnAllow.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.cb_grey)));
                btnAllow.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.grey_heading
                    )
                )


            } else {

                btnAllow.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.text_color_dark)));
                btnAllow.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.white
                    )
                )
            }

        }

        btnAllow.setOnClickListener(View.OnClickListener {
            if (permission2_cb.isChecked && permission1_cb.isChecked) {
                ActivityCompat.requestPermissions(this, PERMISSIONS2, PERMSSIONS2)

            } else if (permission2_cb.isChecked) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMSSIONS2)

            }

        })

        read_contacts.setOnClickListener(View.OnClickListener {
            permission2_cb.isChecked = !permission2_cb.isChecked
        })
        location_permissions.setOnClickListener(View.OnClickListener {
            permission1_cb.isChecked = !permission1_cb.isChecked
        })
        showPrivacyPolicyAndTermsAndConditions()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode == PERMSSIONS2 && hasPermissions(this, *PERMISSIONS)) {
            intentToActivity(HomeView::class.java)
        } else {
            Utility.showToast("Permission is required")
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    private fun intentToActivity(aClass: Class<*>, type: String? = null) {
        val intent = Intent(this, aClass)
        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, type)
        startActivity(intent)
        finish()
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_permission_activity
    }

    override fun getViewModel(): PermissionViewModel {
        mViewModel = ViewModelProvider(this).get(PermissionViewModel::class.java)
        return mViewModel

    }

    private fun showPrivacyPolicyAndTermsAndConditions() {
        val text = resources.getString(
            R.string.permissions_terms,
            resources.getString(R.string.terms_and_conditions),
            resources.getString(R.string.privacy_policy)
        )
        val privacyPolicyText = resources.getString(R.string.privacy_policy)
        val tAndCText = resources.getString(R.string.terms_and_conditions)
        val privacyPolicyStarIndex = text.indexOf(privacyPolicyText)
        val privacyPolicyEndIndex = privacyPolicyStarIndex + privacyPolicyText.length
        val tAndCStartIndex = text.indexOf(tAndCText)
        val tAndCEndIndex = tAndCStartIndex + tAndCText.length
        val ss = SpannableString(text);
        ss.setSpan(

            MyStoreClickableSpan(1, object : ClickableSpanListener {
                override fun onPositionClicked(pos: Int) {
                    openWebPageFor(
                        getString(R.string.privacy_policy),
                        "https://www.fypmoney.in/fyp/privacy-policy/"
                    )

                }
            }),
            privacyPolicyStarIndex,
            privacyPolicyEndIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        ss.setSpan(
            MyStoreClickableSpan(2, object : ClickableSpanListener {
                override fun onPositionClicked(pos: Int) {
                    openWebPageFor(
                        getString(R.string.terms_and_conditions),
                        "https://www.fypmoney.in/fyp/terms-of-use/"
                    )
                }
            }),
            tAndCStartIndex,
            tAndCEndIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tvTerms.text = ss
        tvTerms.highlightColor = Color.TRANSPARENT
        tvTerms.movementMethod = LinkMovementMethod.getInstance()
    }


}

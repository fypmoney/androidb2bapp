package com.fypmoney.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.databinding.ViewAddMemberBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.DialogUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.adapter.CountryCodeArrayAdapter
import com.fypmoney.viewmodel.AddMemberViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_login.*

/*
* This class is used as Add member Screen
* */
class AddMemberView : BaseActivity<ViewAddMemberBinding, AddMemberViewModel>(),
    DialogUtils.OnAlertDialogClickListener {
    private lateinit var mViewModel: AddMemberViewModel
    private lateinit var mViewBinding: ViewAddMemberBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_add_member
    }

    override fun getViewModel(): AddMemberViewModel {
        mViewModel = ViewModelProvider(this).get(AddMemberViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()
        setToolbarAndTitle(
            context = this@AddMemberView,
            toolbar = toolbar,
            isBackArrowVisible = true,
            toolbarTitle = getString(R.string.add_member_heading)
        )
        setObserver()
        setCountryCodeAdapter(applicationContext, mViewBinding.spCountryCode)
        spCountryCode.isEnabled=false
       mViewModel.setResponseAfterContactSelected(intent.getParcelableExtra<ContactEntity>(AppConstants.CONTACT_SELECTED_RESPONSE))

    }

    private fun setCountryCodeAdapter(context: Context, spCountryCode: AppCompatSpinner) {
        val adapterCountryCode = CountryCodeArrayAdapter(
            context,
            R.layout.spinner_item,
            AppConstants.countryCodeList
        )
        with(spCountryCode) {
            this.adapter = adapterCountryCode
            setSelection(0, true)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }
        }

    }

    /*
      * Method to set the length of mobile field
      * */
    fun setMobileLength(position: Int) {
        mViewModel.minTextLength = AppConstants.countryCodeList[position].minLen
        mViewModel.maxTextLength = AppConstants.countryCodeList[position].maxLen
        Utility.setEditTextMaxLength(mViewBinding.etMobileNo, mViewModel.maxTextLength)
        mViewModel.selectedCountryCode.set(AppConstants.countryCodeList[position].dialCode)
    }


    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        mViewModel.onFromContactClicked.observe(this) {
            if (it) {
                intentToActivity(ContactView::class.java)
                mViewModel.onFromContactClicked.value = false
            }
        }

        mViewModel.onAddMemberClicked.observe(this) {
            if (it) {
                mViewModel.onAddMemberClicked.value = false
            }
        }
        mViewModel.onIsAppUser.observe(this) {
            when (it) {
                AppConstants.API_FAIL -> {
                    intentToActivity(InviteMemberView::class.java)

                    /*  DialogUtils.showConfirmationDialog(
                          context = this,
                          title = "",
                          message = mViewModel.mobile.value + " " + getString(R.string.invite_user_dialog_title),
                          positiveButtonText = getString(R.string.invite_text),
                          negativeButtonText = getString(R.string.cancel_btn_text),
                          uniqueIdentifier = "",
                          onAlertDialogClickListener = this, isNegativeButtonRequired = true
                      )*/
                }
                AppConstants.API_SUCCESS -> {
                    askForDevicePassword()
                }


            }
        }

        mViewModel.onAddMember.observe(this)
        {
            when (it) {
                AppConstants.API_SUCCESS -> {
                    intentToActivity(StayTunedView::class.java)

                }
                AppConstants.API_FAIL -> {


                }
            }

        }
    }

    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(aClass: Class<*>) {
        startActivity(Intent(this@AddMemberView, aClass))
    }

    /*
    * This method is used to call the Broadcast receiver
    * */
    private fun callBroadCast() {
        LocalBroadcastManager.getInstance(applicationContext)
            .sendBroadcast(Intent(AppConstants.AFTER_ADD_MEMBER_BROADCAST_NAME))
        finish()
    }

    override fun onPositiveButtonClick(uniqueIdentifier: String) {
        inviteUser()
    }

    override
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AppConstants.DEVICE_SECURITY_REQUEST_CODE -> {
                when (resultCode) {
                    RESULT_OK -> {
                        runOnUiThread {
                            mViewModel.callAddMemberApi()

                        }

                    }

                }
            }
        }
    }
}

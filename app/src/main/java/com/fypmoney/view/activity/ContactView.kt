package com.fypmoney.view.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.databinding.ViewContactsBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.DialogUtils
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.fragment.AddMemberScreen
import com.fypmoney.view.fragment.AddMoneyScreen
import com.fypmoney.viewmodel.ContactViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_user_feeds.*
import java.lang.Exception

/*
* This is used to handle contacts
* */
class ContactView : BaseActivity<ViewContactsBinding, ContactViewModel>(),
    DialogUtils.OnAlertDialogClickListener {
    private lateinit var mViewModel: ContactViewModel

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_contacts
    }

    override fun getViewModel(): ContactViewModel {
        mViewModel = ViewModelProvider(this).get(ContactViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarAndTitle(
            context = this@ContactView,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.select_member)
        )
        setObserver()
        checkAndAskPermission()
    }


    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        mViewModel.onSelectClicked.observe(this) {
            intentToActivity(
                contactEntity = mViewModel.selectedContactList[0]!!,
                aClass = AddMemberView::class.java
            )
        }

        mViewModel.onIsAppUserClicked.observe(this) {
            if (it) {
                inviteUser()
                mViewModel.onIsAppUserClicked.value = false
            }
        }
        mViewModel.emptyContactListError.observe(this) {
            if (it) {
                DialogUtils.showConfirmationDialog(
                    context = this,
                    title = "",
                    message = getString(R.string.empty_contact_error),
                    positiveButtonText = getString(R.string.cancel_btn_text),
                    negativeButtonText = getString(R.string.cancel_btn_text),
                    uniqueIdentifier = "",
                    onAlertDialogClickListener = this, isNegativeButtonRequired = false
                )
                mViewModel.emptyContactListError.value = false
            }
        }

    }

    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(contactEntity: ContactEntity, aClass: Class<*>) {
        val intent = Intent(this@ContactView, aClass)
        intent.putExtra(AppConstants.CONTACT_SELECTED_RESPONSE, contactEntity)
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (permission in permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                //denied
                Utility.showToast(getString(R.string.permission_required))
                requestPermission()
            } else {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        permission
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    //allow
                    mViewModel.progressDialog.value = true
                    mViewModel.callContactSyncApi()
                } else {
                    //set to never ask again
                    SharedPrefUtils.putBoolean(
                        applicationContext,
                        SharedPrefUtils.SF_KEY_STORAGE_PERMANENTLY_DENY,
                        true
                    )
                }
            }
        }


    }

    /*
    * This method is used to check for permissions
    * */
    private fun checkAndAskPermission() {
        when (checkPermission()) {
            true -> {
                mViewModel.progressDialog.value = true
                mViewModel.callContactSyncApi()
            }
            else -> {
                requestPermission()
            }
        }
    }


    /*
    * This method is used to call the Broadcast receiver
    * */
    private fun callBroadCast(contactEntity: ContactEntity) {
        val intent = Intent(AppConstants.CONTACT_BROADCAST_NAME)
        intent.putExtra(
            AppConstants.CONTACT_BROADCAST_KEY,
            contactEntity
        )
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
        finish()
    }

    override fun onPositiveButtonClick(uniqueIdentifier: String) {
        finish()
    }

    override fun onTryAgainClicked() {
        mViewModel.callContactSyncApi()
    }


}

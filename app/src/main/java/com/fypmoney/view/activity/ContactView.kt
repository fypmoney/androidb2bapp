package com.fypmoney.view.activity

import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
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
import com.fypmoney.view.fragment.InviteBottomSheet
import com.fypmoney.viewmodel.ContactViewModel
import kotlinx.android.synthetic.main.toolbar.*

/*
* This is used to handle contacts
* */
class ContactView : BaseActivity<ViewContactsBinding, ContactViewModel>(),
    DialogUtils.OnAlertDialogClickListener, InviteBottomSheet.OnShareClickListener,
    Utility.OnAllContactsAddedListener {
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
        Log.d("contactlist", "1ee0")
    }


    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {

        mViewModel.onItemClicked.observe(this) {

            intentToActivity(
                contactEntity = it,
                aClass = AddMemberView::class.java
            )

        }
        mViewModel.onIsAppUserClicked.observe(this) {
            if (it) {
                if (Utility.getCustomerDataFromPreference()?.postKycScreenCode != null && Utility.getCustomerDataFromPreference()?.postKycScreenCode == "1") {
                    inviteUser()
                } else {
                    callInviteBottomSheet()

                }

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
                requestPermission(android.Manifest.permission.READ_CONTACTS)
            } else {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        permission
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    //allow
                    Utility.getAllContactsInList(
                        contentResolver,
                        this,
                        contactRepository = mViewModel.contactRepository
                    )
                    mViewModel.progressDialog.value = true

                } else {
                    //set to never ask again
                    Utility.showToast(getString(R.string.please_allow_us_to_read_your_contacts))
                    Utility.goToAppSettingsPermission(this@ContactView,
                        AppConstants.PERMISSION_CODE
                    )
                }
            }
        }


    }

    /*
    * This method is used to check for permissions
    * */
    private fun checkAndAskPermission() {
        when (checkPermission(android.Manifest.permission.READ_CONTACTS)) {
            true -> {
                mViewModel.progressDialog.value = true
                Utility.getAllContactsInList(
                    contentResolver,
                    this,
                    contactRepository = mViewModel.contactRepository
                )

            }
            else -> {
                requestPermission(android.Manifest.permission.READ_CONTACTS)
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

    /*
 * This method is used to call invite sheet
 * */
    private fun callInviteBottomSheet() {
        val bottomSheet =
            InviteBottomSheet(getSystemService(CLIPBOARD_SERVICE) as ClipboardManager, this)
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(supportFragmentManager, "InviteView")
    }

    override fun onShareClickListener(referralCode: String) {
        SharedPrefUtils.getString(this,SharedPrefUtils.SF_KEY_REFERAL_GLOBAL_MSG)?.let {
            shareInviteCodeFromReferal(it)
        }
    }

    override fun onAllContactsSynced(contactEntity: MutableList<ContactEntity>?) {
        mViewModel.callContactSyncApi()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== AppConstants.PERMISSION_CODE){
            if(resultCode== RESULT_OK){
                checkAndAskPermission()
            }
        }
    }
}

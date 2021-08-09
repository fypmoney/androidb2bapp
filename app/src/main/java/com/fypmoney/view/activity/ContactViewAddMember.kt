package com.fypmoney.view.activity

import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
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
class ContactViewAddMember : BaseActivity<ViewContactsBinding, ContactViewModel>(),
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
            context = this@ContactViewAddMember,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.select_member)
        )
        setObserver()
        checkAndAskPermission()
    }



    private fun setObserver() {
        mViewModel.onSelectClicked.observe(this) {

            val resultIntent = Intent()


//            Toast.makeText(this,mViewModel.selectedContactList[0].firstName,Toast.LENGTH_SHORT).show()
            resultIntent.putExtra(AppConstants.CONTACT_SELECTED_RESPONSE, mViewModel.selectedContactList[0])
            setResult(13, resultIntent)
            finish()

        }

        mViewModel.onIsAppUserClicked.observe(this) {
            if (it) {
             callInviteBottomSheet()
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
                    mViewModel.progressDialog.value = true
                    Utility.getAllContactsInList(
                        contentResolver,
                        this,
                        contactRepository = mViewModel.contactRepository
                    )
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
        when (checkPermission(android.Manifest.permission.READ_CONTACTS)) {
            true -> {
                mViewModel.progressDialog.value = true
                mViewModel.callContactSyncApi()
            }
            else -> {
                requestPermission(android.Manifest.permission.READ_CONTACTS)
            }
        }
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
        inviteUser()
    }

    override fun onAllContactsSynced(contactEntity: MutableList<ContactEntity>?) {
        mViewModel.callContactSyncApi()
    }
}

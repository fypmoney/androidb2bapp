package com.fypmoney.view.activity

import android.Manifest
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.databinding.ViewContactListBinding
import com.fypmoney.databinding.ViewContactsBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.AppConstants.PERMISSION_CODE
import com.fypmoney.util.DialogUtils
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.fragment.InviteBottomSheet
import com.fypmoney.view.fragment.InviteMemberBottomSheet
import com.fypmoney.viewmodel.ContactListViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_contact_list.*
import kotlinx.android.synthetic.main.view_contacts.*

/*
* This is used to handle contacts
* */

class ContactListView : BaseActivity<ViewContactListBinding, ContactListViewModel>(),
    DialogUtils.OnAlertDialogClickListener, InviteBottomSheet.OnShareClickListener,
    InviteMemberBottomSheet.OnInviteButtonClickListener, Utility.OnAllContactsAddedListener {
    private lateinit var mViewModel: ContactListViewModel

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_contact_list
    }

    override fun getViewModel(): ContactListViewModel {
        mViewModel = ViewModelProvider(this).get(ContactListViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarAndTitle(
            context = this@ContactListView,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.pay_title),
            backArrowTint = Color.WHITE,
            titleColor = Color.WHITE
        )
        toolbar.setBackgroundColor(ContextCompat.getColor(this,R.color.black))
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
                callInviteMemberBottomSheet(ApiConstant.API_CHECK_IS_APP_USER)
                mViewModel.onIsAppUserClicked.value = false
            }
        }

        mViewModel.onInviteClicked.observe(this) {
            if (it) {
                if (Utility.getCustomerDataFromPreference()?.postKycScreenCode != null && Utility.getCustomerDataFromPreference()?.postKycScreenCode == "1") {

                    inviteUser()
                } else {
                    callInviteBottomSheet()
                }

                mViewModel.onIsAppUserClicked.value = false
            }
        }

        mViewModel.onItemClicked.observe(this) {
            intentToActivity(
                contactEntity = it,
                PayRequestProfileView::class.java
            )

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
        mViewModel.fetchBalanceLoading.observe(this) {
            if (it) {
                amountFetching.clearAnimation()
                amountFetching.visibility = View.GONE
                mViewModel.fetchBalanceLoading.value = false
            }
        }
    }

    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(contactEntity: ContactEntity, aClass: Class<*>) {
        val intent = Intent(this@ContactListView, aClass)
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
                requestPermission(Manifest.permission.READ_CONTACTS)
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
                    Utility.showToast(getString(R.string.please_allow_us_to_read_your_contacts))
                    Utility.goToAppSettingsPermission(this@ContactListView,PERMISSION_CODE)
                }
            }
        }


    }

    /*
    * This method is used to check for permissions
    * */
    private fun checkAndAskPermission() {
        when (checkPermission(Manifest.permission.READ_CONTACTS)) {
            true -> {
                Utility.getAllContactsInList(
                    contentResolver,
                    this,
                    contactRepository = mViewModel.contactRepository
                )
            }
            else -> {
                requestPermission(Manifest.permission.READ_CONTACTS)
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
    private fun callInviteMemberBottomSheet(type: String) {
        val bottomSheet =
            InviteMemberBottomSheet(type, mViewModel.searchedContact.get(), this)
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(supportFragmentManager, "InviteMemberView")
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

    override fun onInviteButtonClick() {
        if (Utility.getCustomerDataFromPreference()?.postKycScreenCode != null && Utility.getCustomerDataFromPreference()?.postKycScreenCode == "1") {

            inviteUser()
        } else {
            callInviteBottomSheet()
        }

    }

    override fun onAllContactsSynced(contactEntity: MutableList<ContactEntity>?) {
        mViewModel.callContactSyncApi()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== PERMISSION_CODE){
            if(resultCode== RESULT_OK){
                checkAndAskPermission()
            }
        }
    }
}

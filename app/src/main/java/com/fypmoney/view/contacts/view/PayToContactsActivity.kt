package com.fypmoney.view.contacts.view

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
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.databinding.ActivityPayToContactsBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.util.AppConstants
import com.fypmoney.util.AppConstants.PERMISSION_CODE
import com.fypmoney.util.DialogUtils
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.activity.AddMemberView
import com.fypmoney.view.activity.PayRequestProfileView
import com.fypmoney.view.contacts.adapter.ContactsAdapter
import com.fypmoney.view.fragment.InviteBottomSheet
import com.fypmoney.view.fragment.InviteMemberBottomSheet
import com.fypmoney.view.contacts.viewmodel.PayToContactsActivityVM
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_pay_to_contacts.*
import kotlinx.android.synthetic.main.toolbar.*

/*
* This is used to handle contacts
* */

class PayToContactsActivity : BaseActivity<ActivityPayToContactsBinding, PayToContactsActivityVM>(),
    DialogUtils.OnAlertDialogClickListener, InviteBottomSheet.OnShareClickListener,
    InviteMemberBottomSheet.OnInviteButtonClickListener, Utility.OnAllContactsAddedListener {

    private lateinit var mViewModel: PayToContactsActivityVM
    private lateinit var payToContactsBinding: ActivityPayToContactsBinding
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_pay_to_contacts
    }

    override fun getViewModel(): PayToContactsActivityVM {
        mViewModel = ViewModelProvider(this).get(PayToContactsActivityVM::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        payToContactsBinding = getViewDataBinding()
        setToolbarAndTitle(
            context = this@PayToContactsActivity,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.pay_title),
            backArrowTint = Color.WHITE,
            titleColor = Color.WHITE
        )
        toolbar.setBackgroundColor(ContextCompat.getColor(this,R.color.black))
        setupRecyclerView()
        setObserver()
    }

    override fun onResume() {
        super.onResume()
        checkAndAskPermission()

    }

    private fun setupRecyclerView() {
        payToContactsBinding.contactListRv.adapter = ContactsAdapter(lifecycleOwner = this,onContactClick={

        })
    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {

        mViewModel.state.observe(this){
            handelState(it)
        }
    }

    private fun handelState(it: PayToContactsActivityVM.PayToContactsState?) {
        when(it){
            PayToContactsActivityVM.PayToContactsState.BalanceError -> {

            }
            PayToContactsActivityVM.PayToContactsState.BalanceLoading -> {
                amountFetching.toVisible()
            }
            PayToContactsActivityVM.PayToContactsState.Error -> {

            }
            PayToContactsActivityVM.PayToContactsState.Loading -> {
                payToContactsBinding.notDataInPhoneBookTv.toGone()
                payToContactsBinding.contactListRv.toGone()
                payToContactsBinding.contactIsLoadingSfl.toVisible()
            }
            is PayToContactsActivityVM.PayToContactsState.SuccessBalanceState -> {
                amountFetching.clearAnimation()
                amountFetching.toGone()
                amount.text = String.format(getString(R.string.amount_with_currency),it.balance)
            }
            null -> TODO()
            PayToContactsActivityVM.PayToContactsState.ContactsIsEmpty -> {
                payToContactsBinding.contactListRv.toGone()
                payToContactsBinding.contactIsLoadingSfl.toGone()
                payToContactsBinding.notDataInPhoneBookTv.toVisible()
            }
            is PayToContactsActivityVM.PayToContactsState.SuccessContacts -> {
                payToContactsBinding.notDataInPhoneBookTv.toGone()
                payToContactsBinding.contactListRv.toVisible()
                payToContactsBinding.contactIsLoadingSfl.toGone()
                (payToContactsBinding.contactListRv.adapter as ContactsAdapter).submitList(it.contacts)
            }
        }
    }

    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(contactEntity: ContactEntity, aClass: Class<*>) {
        val intent = Intent(this@PayToContactsActivity, aClass)
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
                    Utility.goToAppSettingsPermission(this@PayToContactsActivity,PERMISSION_CODE)
                }
            }
        }


    }

    /*
    * This method is used to check for permissions
    * */
    private fun checkAndAskPermission() {/*
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
        }*/

        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_CONTACTS
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        Utility.getAllContactsInList(
                            contentResolver,
                            this@PayToContactsActivity,
                            contactRepository = mViewModel.contactRepository
                        )
                    }
                    if (report.isAnyPermissionPermanentlyDenied) {
                        showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    override fun onPositiveButtonClick(uniqueIdentifier: String) {
        finish()
    }

    override fun onTryAgainClicked() {
        //mViewModel.callContactSyncApi()
    }

    /*
   * This method is used to call invite sheet
   * */
    private fun callInviteMemberBottomSheet(type: String) {
        val bottomSheet =
            InviteMemberBottomSheet(type, "", this)
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
        SharedPrefUtils.getString(this,SharedPrefUtils.SF_KEY_REFERAL_GLOBAL_MSG)?.let {
            shareInviteCodeFromReferal(it)
        }
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

    private fun showSettingsDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@PayToContactsActivity)
        builder.setTitle(getString(R.string.dialog_permission_title))
        builder.setMessage(getString(R.string.dialog_permission_message))
        builder.setPositiveButton(getString(R.string.go_to_settings)) { dialog, which ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton(getString(android.R.string.cancel)) { dialog, which -> dialog.cancel() }
        builder.show()
    }
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }
}

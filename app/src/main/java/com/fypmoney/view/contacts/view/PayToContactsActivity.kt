package com.fypmoney.view.contacts.view

import android.Manifest
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.databinding.ActivityPayToContactsBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.util.AppConstants
import com.fypmoney.util.DialogUtils
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.activity.AddMemberView
import com.fypmoney.view.activity.PayRequestProfileView
import com.fypmoney.view.contacts.adapter.ContactsAdapter
import com.fypmoney.view.contacts.model.CONTACT_ACTIVITY_UI_MODEL
import com.fypmoney.view.contacts.model.ContactActivityActionEvent
import com.fypmoney.view.contacts.model.ContactsActivityUiModel
import com.fypmoney.view.contacts.viewmodel.PayToContactsActivityVM
import com.fypmoney.view.fragment.InviteBottomSheet
import com.fypmoney.view.fragment.InviteMemberBottomSheet
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_pay_to_contacts.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ObsoleteCoroutinesApi

/*
* This is used to handle contacts
* */

@ObsoleteCoroutinesApi
@FlowPreview
class PayToContactsActivity : BaseActivity<ActivityPayToContactsBinding, PayToContactsActivityVM>(),
    DialogUtils.OnAlertDialogClickListener, InviteBottomSheet.OnShareClickListener,
    InviteMemberBottomSheet.OnInviteButtonClickListener {

    private lateinit var mViewModel: PayToContactsActivityVM
    private lateinit var payToContactsBinding: ActivityPayToContactsBinding
    private val TAG = PayToContactsActivity::class.java.simpleName

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
        mViewModel.contactsActivityUiModel = intent.getParcelableExtra(CONTACT_ACTIVITY_UI_MODEL)!!
        mViewModel.checkNeedToShowBalanceOnScreen()
        setToolbarAndTitle(
            context = this@PayToContactsActivity,
            toolbar = toolbar,
            isBackArrowVisible = true,
            toolbarTitle = mViewModel.contactsActivityUiModel.toolBarTitle,
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
        payToContactsBinding.contactListRv.adapter = ContactsAdapter(lifecycleOwner = this,
            onContactClick={
                mViewModel.selectedContacts = it
                mViewModel.callIsAppUserApi(it.mobileNumber)
        })
    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {

        mViewModel.state.observe(this){
            handelState(it)
        }
        mViewModel.event.observe(this){
            handelEvent(it)
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
            PayToContactsActivityVM.PayToContactsState.HideBalanceView -> {
                payToContactsBinding.avilableBalanceCl.toGone()
            }
            null -> {

            }
            PayToContactsActivityVM.PayToContactsState.ShowBalanceView -> {
                payToContactsBinding.avilableBalanceCl.toVisible()
            }
        }
    }

    private fun handelEvent(it: PayToContactsActivityVM.PayToContactsEvent?) {
        when(it){
            is PayToContactsActivityVM.PayToContactsEvent.SelectedContactUserIsFyper -> {
                when(mViewModel.contactsActivityUiModel.contactClickAction){
                    ContactActivityActionEvent.AddMember -> {
                        intentToActivity(
                            contactEntity = it.contactEntity,
                            aClass = AddMemberView::class.java
                        )
                    }
                    ContactActivityActionEvent.PayToContact -> {
                        intentToActivity(it.contactEntity, PayRequestProfileView::class.java)
                    }
                }
            }
            is PayToContactsActivityVM.PayToContactsEvent.SelectedContactUserIsNotFyper -> {
                if (Utility.getCustomerDataFromPreference()?.postKycScreenCode != null && Utility.getCustomerDataFromPreference()?.postKycScreenCode == "1") {
                    inviteUser()
                } else {
                    callInviteBottomSheet()
                }
            }
            null -> TODO()
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


    /*
    * This method is used to check for permissions
    * */
    private fun checkAndAskPermission() {
        Dexter.withContext(this)
            .withPermission(
                Manifest.permission.READ_CONTACTS
            )
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    payToContactsBinding.permissionRequiredTv.toGone()
                    mViewModel.fetchContactShow(this@PayToContactsActivity.contentResolver)
                }
                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    if(p0?.isPermanentlyDenied!!){
                        payToContactsBinding.permissionRequiredTv.toVisible()
                    }else{
                        p0.requestedPermission
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    p1?.continuePermissionRequest()
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

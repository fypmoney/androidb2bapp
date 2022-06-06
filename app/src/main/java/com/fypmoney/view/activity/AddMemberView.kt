package com.fypmoney.view.activity

import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.databinding.ViewAddMemberBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.DialogUtils
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.contacts.model.CONTACT_ACTIVITY_UI_MODEL
import com.fypmoney.view.contacts.model.ContactActivityActionEvent
import com.fypmoney.view.contacts.model.ContactsActivityUiModel
import com.fypmoney.view.contacts.view.PayToContactsActivity
import com.fypmoney.view.fragment.InviteBottomSheet
import com.fypmoney.view.fragment.InviteMemberBottomSheet
import com.fypmoney.view.fragment.StayTunedBottomSheet
import com.fypmoney.viewmodel.AddMemberViewModel
import kotlinx.android.synthetic.main.bottom_sheet_invite.view.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_login.*

/*
* This class is used as Add member Screen
* */
class AddMemberView : BaseActivity<ViewAddMemberBinding, AddMemberViewModel>(),
    DialogUtils.OnAlertDialogClickListener,InviteBottomSheet.OnShareClickListener ,InviteMemberBottomSheet.OnInviteButtonClickListener{
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
        mViewModel.setResponseAfterContactSelected(intent.getParcelableExtra(AppConstants.CONTACT_SELECTED_RESPONSE))

    }

/*
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
*/

    /*
      * Method to set the length of mobile field
      * */


    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        mViewModel.onFromContactClicked.observe(this) {
            if (it) {
                //intentToActivity(ContactView::class.java)
                val intent = Intent(this@AddMemberView, PayToContactsActivity::class.java)
                intent.putExtra(
                    CONTACT_ACTIVITY_UI_MODEL, ContactsActivityUiModel(toolBarTitle = getString(R.string.select_member),
                    showLoadingBalance = false,contactClickAction = ContactActivityActionEvent.PayToContact)
                )
                startActivity(intent)
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
                    callInviteMemberBottomSheet(ApiConstant.API_CHECK_IS_APP_USER)

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
                    callStayTunedBottomSheet()

                }
                else->{
                    callInviteMemberBottomSheet(it)
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
       // callInviteBottomSheet()
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

    /*
* This method is used to call leave member
* */
    private fun callStayTunedBottomSheet() {
        val bottomSheet =
            StayTunedBottomSheet()
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(supportFragmentManager, "StayTunedView")
    }

    /*
* This method is used to call leave member
* */
    private fun callInviteMemberBottomSheet(type:String) {
        val bottomSheet =
            InviteMemberBottomSheet(type,onInviteButtonClickListener = this)
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(supportFragmentManager, "InviteMemberView")
    }

    /*
* This method is used to call leave member
* */
    private fun callInviteBottomSheet() {
        val bottomSheet =
            InviteBottomSheet(getSystemService(CLIPBOARD_SERVICE) as ClipboardManager,this)
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(supportFragmentManager, "InviteView")
    }

    override fun onShareClickListener(referralCode: String) {
        SharedPrefUtils.getString(this, SharedPrefUtils.SF_KEY_REFERAL_GLOBAL_MSG)?.let {
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
}

package com.fypmoney.view.fragment

import android.app.Activity.RESULT_OK
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.databinding.ViewAddMemberBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.DialogUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.activity.ContactView
import com.fypmoney.view.activity.InviteMemberView
import com.fypmoney.view.activity.StayTunedView
import com.fypmoney.view.adapter.CountryCodeArrayAdapter
import com.fypmoney.viewmodel.AddMemberViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_login.*

/*
* This class is used as Add member Screen
* */
class AddMemberScreen : BaseFragment<ViewAddMemberBinding, AddMemberViewModel>(),
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()
        mViewBinding.viewModel = mViewModel
        setToolbarAndTitle(
            context =requireContext(),
            toolbar = toolbar,
            isBackArrowVisible = true
        )

        // set the is Guarantor value
   /*     when (intent.getStringExtra(AppConstants.CREATE_ACCOUNT_SUCCESS)) {
            AppConstants.CREATE_ACCOUNT_SUCCESS -> {
                mViewModel.isGuarantor.set(AppConstants.YES)
            }
            else -> {
                mViewModel.isGuarantor.set(AppConstants.NO)

            }
        }*/

        // register broadcast receiver to handle user active loyalty points
        val lbm = LocalBroadcastManager.getInstance(requireContext())
        lbm.registerReceiver(receiver, IntentFilter(AppConstants.CONTACT_BROADCAST_NAME))
        setObserver()
        setCountryCodeAdapter(requireContext(), mViewBinding.spCountryCode)
        spCountryCode.isEnabled=false

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

                    setMobileLength(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }
        }

        setMobileLength(spCountryCode.selectedItemPosition)
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
        mViewModel.onFromContactClicked.observe(viewLifecycleOwner) {
            if (it) {
                intentToActivity(ContactView::class.java)
                mViewModel.onFromContactClicked.value = false
            }
        }

        mViewModel.onAddMemberClicked.observe(viewLifecycleOwner) {
            if (it) {
                mViewModel.onAddMemberClicked.value = false
            }
        }
        mViewModel.onIsAppUser.observe(viewLifecycleOwner) {
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

        mViewModel.onAddMember.observe(viewLifecycleOwner)
        {
            when (it) {
                AppConstants.API_SUCCESS -> {
                    //   callBroadCast()
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
        startActivity(Intent(requireContext(), aClass))
    }

    private var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            mViewModel.contactResult.set(intent.getParcelableExtra(AppConstants.CONTACT_BROADCAST_KEY))
            if (mViewModel.contactResult.get()!!.lastName.isNullOrEmpty()) {
                mViewModel.name.set(mViewModel.contactResult.get()!!.firstName)
            } else {
                mViewModel.name.set(mViewModel.contactResult.get()!!.firstName + " " + mViewModel.contactResult.get()!!.lastName)
            }
            mViewModel.mobile.value =
                intent.getParcelableExtra<ContactEntity>(AppConstants.CONTACT_BROADCAST_KEY)?.contactNumber
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiver)
    }

    override fun onTryAgainClicked() {
    }

    /*
    * This method is used to call the Broadcast receiver
    * */
    private fun callBroadCast() {
        LocalBroadcastManager.getInstance(requireContext())
            .sendBroadcast(Intent(AppConstants.AFTER_ADD_MEMBER_BROADCAST_NAME))
        requireActivity().finish()
    }

    override fun onPositiveButtonClick(uniqueIdentifier: String) {
      //  inviteUser()
    }

    override
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AppConstants.DEVICE_SECURITY_REQUEST_CODE -> {
                when (resultCode) {
                    RESULT_OK -> {
                        requireActivity().runOnUiThread {
                            mViewModel.callAddMemberApi()

                        }

                    }

                }
            }
        }
    }
}

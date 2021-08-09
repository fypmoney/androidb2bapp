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
            isBackArrowVisible = false
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

          setObserver()

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
        Utility.setEditTextMaxLength(et_start, mViewModel.maxTextLength)
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
                    intentToActivity(StayTunedView::class.java)

                }
            else->{

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



    override fun onTryAgainClicked() {
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

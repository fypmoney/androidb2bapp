package com.fypmoney.view.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.databinding.FragmentFamilySettingsBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.contacts.model.CONTACT_ACTIVITY_UI_MODEL
import com.fypmoney.view.contacts.model.ContactActivityActionEvent
import com.fypmoney.view.contacts.model.ContactsActivityUiModel
import com.fypmoney.view.contacts.view.PayToContactsActivity
import com.fypmoney.view.fragment.LeaveFamilyBottomSheet
import com.fypmoney.view.fragment.UpdateFamilyNameBottomSheet
import com.fypmoney.viewmodel.FamilySettingsFragmentVM
import kotlinx.android.synthetic.main.toolbar.*


/*
* This class is used as Home Screen
*
* */

class FamilySettingsFragment : BaseFragment<FragmentFamilySettingsBinding, FamilySettingsFragmentVM>(),
    UpdateFamilyNameBottomSheet.OnUpdateFamilyClickListener,
    LeaveFamilyBottomSheet.OnLeaveFamilyClickListener {
    private lateinit var mViewModel: FamilySettingsFragmentVM
    private lateinit var mViewBinding: FragmentFamilySettingsBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_family_settings
    }

    override fun getViewModel(): FamilySettingsFragmentVM {
        mViewModel = ViewModelProvider(this).get(FamilySettingsFragmentVM::class.java)
        return mViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()

        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar, backArrowTint = Color.BLACK,
            titleColor = Color.BLACK,
            isBackArrowVisible = true,
            toolbarTitle = getString(R.string.family)
        )
        setObserver()
    }

    override fun onResume() {
        super.onResume()
        mViewModel.callGetMemberApi()
    }

    /*var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            mViewModel.callGetMemberApi()
        }
    }*/

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        mViewModel.onViewAllClicked.observe(viewLifecycleOwner) {
            if (it) {
                intentToActivity(MemberView::class.java)
                mViewModel.onViewAllClicked.value = false
            }
        }

        mViewModel.onAddMemberClicked.observe(viewLifecycleOwner) {
            if (it) {
                //intentToAddMemberActivity(ContactView::class.java)
                val intent = Intent(requireActivity(), PayToContactsActivity::class.java)
                intent.putExtra(
                    CONTACT_ACTIVITY_UI_MODEL, ContactsActivityUiModel(toolBarTitle = getString(R.string.select_member),
                        showLoadingBalance = false,contactClickAction = ContactActivityActionEvent.AddMember)
                )
                startActivity(intent)
                mViewModel.onAddMemberClicked.value = false
            }
        }
        mViewModel.onFamilyMemberClicked.observe(viewLifecycleOwner) {
            var contact = ContactEntity()
            contact.userId = it.userId.toString()
            contact.contactNumber = it.mobileNo
            contact.firstName = it.name
            contact.profilePicResourceId = it.profilePicResourceId

//            contact.lastName=it.familyName


            intentToActivity(
                contactEntity = contact,
                aClass = PayRequestProfileView::class.java, ""
            )


        }

        mViewModel.onChoresClicked.observe(viewLifecycleOwner) {
            if (it) {
                intentToAddMemberActivity(ChoresActivity::class.java)
                mViewModel.onChoresClicked.value = false
            }
        }
        mViewModel.onLeaveFamilySuccess.observe(viewLifecycleOwner) {
            if (it) {
                SharedPrefUtils.putString(
                    requireContext(),
                    SharedPrefUtils.SF_KEY_USER_FAMILY_NAME,
                    null
                )
                mViewModel.username.set(
                    Utility.getCustomerDataFromPreference()?.firstName + requireActivity().resources.getString(
                        R.string.family_settings_family_fypers
                    )
                )
                mViewModel.callGetMemberApi()
                mViewModel.onLeaveFamilySuccess.value = false
            }
        }
        mViewModel.onEditFamilyNameClicked.observe(viewLifecycleOwner) {
            if (it) {
                callBottomSheet()
                mViewModel.onEditFamilyNameClicked.value = false
            }
        }

        mViewModel.onLeaveFamilyClicked.observe(viewLifecycleOwner) {
            if (it) {
                callLeaveFamilyBottomSheet()
                mViewModel.onLeaveFamilyClicked.value = false
            }
        }

    }

    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(aClass: Class<*>) {
        val intent = Intent(requireActivity(), aClass)
        requireContext().startActivity(intent)
    }

    /**
     * Method to navigate to the different activity
     */
    private fun intentToAddMemberActivity(aClass: Class<*>) {
        val intent = Intent(requireActivity(), aClass)
        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, "")
        requireContext().startActivity(intent)
    }

    private fun intentToActivity(contactEntity: ContactEntity?, aClass: Class<*>, action: String) {
        val intent = Intent(requireContext(), aClass)
        intent.putExtra(AppConstants.CONTACT_SELECTED_RESPONSE, contactEntity)
        intent.putExtra(AppConstants.WHICH_ACTION, action)
        intent.putExtra(AppConstants.FUND_TRANSFER_QR_CODE, "")
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        //LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiver)
    }

    override fun onTryAgainClicked() {

    }

    override fun onUpdateFamilyButtonClick(familyName: String) {
        mViewModel.changedUserName.set(familyName)
        mViewModel.callUpdateFamilyName()

    }

    /*
* This method is used to call leave member
* */
    private fun callBottomSheet() {
        val bottomSheet =
            UpdateFamilyNameBottomSheet(
                onBottomSheetClickListener = this
            )
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(childFragmentManager, "UpdateFamilyName")
    }

    /*
* This method is used to call leave member
* */
    private fun callLeaveFamilyBottomSheet() {
        val bottomSheet =
            LeaveFamilyBottomSheet(
                this
            )
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(childFragmentManager, "UpdateFamilyName")
    }

    override fun onLeaveClick() {
        mViewModel.callLeaveFamilyApi()
    }
}
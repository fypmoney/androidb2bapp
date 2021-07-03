package com.fypmoney.view.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.ViewFamilySettingsBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.view.fragment.LeaveFamilyBottomSheet
import com.fypmoney.view.fragment.UpdateFamilyNameBottomSheet
import com.fypmoney.viewmodel.FamilySettingsViewModel


/*
* This class is used as Home Screen
* */
class FamilySettingsView : BaseFragment<ViewFamilySettingsBinding, FamilySettingsViewModel>(),
    UpdateFamilyNameBottomSheet.OnUpdateFamilyClickListener,
    LeaveFamilyBottomSheet.OnLeaveFamilyClickListener {
    private lateinit var mViewModel: FamilySettingsViewModel
    private lateinit var mViewBinding: ViewFamilySettingsBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_family_settings
    }

    override fun getViewModel(): FamilySettingsViewModel {
        mViewModel = ViewModelProvider(this).get(FamilySettingsViewModel::class.java)
        return mViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()

        mViewModel.callGetMemberApi()
        val lbm = LocalBroadcastManager.getInstance(requireContext())
        lbm.registerReceiver(receiver, IntentFilter(AppConstants.AFTER_ADD_MEMBER_BROADCAST_NAME))

        setObserver()
    }

    var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            mViewModel.callGetMemberApi()
        }
    }

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
                intentToAddMemberActivity(AddMemberView::class.java)
                mViewModel.onAddMemberClicked.value = false
            }
        }

        mViewModel.onChoresClicked.observe(viewLifecycleOwner) {
            if (it) {
                intentToAddMemberActivity(ChoresActivity::class.java)
                mViewModel.onChoresClicked.value = false
            }
        }
        mViewModel.onLeaveFamilySuccess.observe(viewLifecycleOwner) {
            if (it) {
                intentToActivity(HomeView::class.java)
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

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiver)
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
package com.fypmoney.view.pocketmoneysettings.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentPocketMoneySettingsBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.model.DeletePocketMoneyReminder
import com.fypmoney.util.Utility
import com.fypmoney.view.pocketmoneysettings.adapter.PocketMoneyReminderAdapter
import com.fypmoney.view.pocketmoneysettings.adapter.PocketMoneyReminderUiModel
import com.fypmoney.view.pocketmoneysettings.model.Data
import com.fypmoney.view.pocketmoneysettings.viewmodel.PocketMoneySettingsFragmentVM
import kotlinx.android.synthetic.main.dialog_delete_reminder_confirm.*
import kotlinx.android.synthetic.main.toolbar.*

class PocketMoneySettingsFragment : BaseFragment<FragmentPocketMoneySettingsBinding, PocketMoneySettingsFragmentVM>() {

    private val pocketMoneySettingsFragmentVM by viewModels<PocketMoneySettingsFragmentVM> { defaultViewModelProviderFactory }
    private lateinit var binding: FragmentPocketMoneySettingsBinding
    private lateinit var dialogDisableConfirm: Dialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = getViewDataBinding()

        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = "Pocket money settings",
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )

        pocketMoneySettingsFragmentVM.callPocketMoneyReminderData()

        binding.btnPocketSettingAddNow.setOnClickListener {
            openAddReminderBottomSheet()
        }

        setObserver()

        setUpRecentRecyclerView()

    }

    interface OnClickListener {
        fun onClick()
    }

    private fun openAddReminderBottomSheet() {
        val addReminderBottomSheet = AddNowPocketMoneyBottomSheet()
        addReminderBottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        addReminderBottomSheet.show(childFragmentManager, "AddNowPocketMoneyBottomSheet")
        addReminderBottomSheet.setOnAddReminderActionCompleteListener(addReminderListener)
    }

    private val addReminderListener =
        object : AddNowPocketMoneyBottomSheet.CloseAddBottomActionCompleteListener {
            override fun onOtpSuccessActionComplete(data: Data?) {
                openOtpReminderBottomSheet(data)
            }
        }

    private val notifyAddReminderListener =
        object : OtpReminderBottomSheet.OnOtpVerifyCompleteListener {
            override fun onActionComplete(data: String) {
                pocketMoneySettingsFragmentVM.callPocketMoneyReminderData()
            }
        }

    private fun openOtpReminderBottomSheet(data: Data?) {
        val otpReminderBottomSheet = OtpReminderBottomSheet()
        val bundle = Bundle()
        bundle.putString("otpIdentifier", data?.otpIdentifier)
        bundle.putString("name", data?.name)
        bundle.putString("mobile", data?.mobile)
        data?.amount?.let { bundle.putInt("amount", it) }
        bundle.putString("frequency", data?.frequency)
        otpReminderBottomSheet.arguments = bundle
        otpReminderBottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        otpReminderBottomSheet.show(childFragmentManager, "OtpBottomSheet")
        otpReminderBottomSheet.setOnOtpVerifyCompleteListener(notifyAddReminderListener)

    }

    private fun setObserver() {
        pocketMoneySettingsFragmentVM.stateReminderCoupon.observe(viewLifecycleOwner) {
            handleReminderCouponState(it)
        }

        pocketMoneySettingsFragmentVM.stateReminderDeleteCoupon.observe(viewLifecycleOwner) {
            handleReminderDeleteState(it)
        }
    }

    private fun handleReminderDeleteState(it: PocketMoneySettingsFragmentVM.PocketMoneyReminderDeleteState?) {
        when (it) {
            is PocketMoneySettingsFragmentVM.PocketMoneyReminderDeleteState.Error -> {}
            PocketMoneySettingsFragmentVM.PocketMoneyReminderDeleteState.Loading -> {}
            is PocketMoneySettingsFragmentVM.PocketMoneyReminderDeleteState.Success -> {
                if (dialogDisableConfirm != null && dialogDisableConfirm.isShowing)
                    dialogDisableConfirm.dismiss()
                Utility.showToast("Reminder deleted successfully")
                pocketMoneySettingsFragmentVM.callPocketMoneyReminderData()
            }
            null -> {}
        }
    }

    private fun handleReminderCouponState(it: PocketMoneySettingsFragmentVM.PocketMoneyReminderState?) {
        when (it) {
            is PocketMoneySettingsFragmentVM.PocketMoneyReminderState.Error -> {}
            PocketMoneySettingsFragmentVM.PocketMoneyReminderState.Loading -> {}
            is PocketMoneySettingsFragmentVM.PocketMoneyReminderState.Success -> {
                if (it.dataItem?.isEmpty() == true) {
                    binding.clEmptyPocketMoneyReminder.toVisible()
                    binding.rvPocketMoneyReminder.toGone()
                } else {
                    binding.clEmptyPocketMoneyReminder.toGone()
                    binding.rvPocketMoneyReminder.toVisible()
                }

                (binding.rvPocketMoneyReminder.adapter as PocketMoneyReminderAdapter).submitList(
                    it.dataItem?.toMutableList()?.map {
                        it.let { it1 ->
                            it1?.let { it2 ->
                                PocketMoneyReminderUiModel.fromPocketMoneyReminderItem(
                                    it2
                                )
                            }
                        }
                    }
                )
            }
            null -> {}
        }
    }

    private fun setUpRecentRecyclerView() {
        val pocketMoneyReminderAdapter = PocketMoneyReminderAdapter(
            onClickNotifyDelete = {
                callConfirmDisableNotificationDialog(this.requireContext(), it)
            },
            onClickOpenEditSheet = {
                openEditReminderBottomSheet(it)
            })
        with(binding.rvPocketMoneyReminder) {
            adapter = pocketMoneyReminderAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun openEditReminderBottomSheet(item: PocketMoneyReminderUiModel) {
        val editReminderBottomSheet = EditPocketMoneyBottomSheet()
        val bundle = Bundle()
        bundle.putString("name", item.name)
        bundle.putString("mobile", item.mobile)
        bundle.putInt("amount", item.amount!!)
        bundle.putString("frequency", item.frequency)
        editReminderBottomSheet.arguments = bundle
        editReminderBottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        editReminderBottomSheet.show(childFragmentManager, "EditPocketMoneyBottomSheet")
        editReminderBottomSheet.setOnEditReminderActionCompleteListener(editReminderNotifyListener)
    }

    private val editReminderNotifyListener =
        object : EditPocketMoneyBottomSheet.OnEditReminderActionCompleteListener {
            override fun onEditReminderActionComplete(data: String) {
                pocketMoneySettingsFragmentVM.callPocketMoneyReminderData()
            }
        }

    private fun callConfirmDisableNotificationDialog(context: Context, mobile: String) {

        dialogDisableConfirm = Dialog(context)

        dialogDisableConfirm.setCancelable(false)
        dialogDisableConfirm.setCanceledOnTouchOutside(false)
        dialogDisableConfirm.setContentView(R.layout.dialog_delete_reminder_confirm)

        val wlp = dialogDisableConfirm.window?.attributes
        wlp?.width = ViewGroup.LayoutParams.MATCH_PARENT
        dialogDisableConfirm.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogDisableConfirm.window?.attributes = wlp

        dialogDisableConfirm.btnDeleteNotifications?.setOnClickListener {
            pocketMoneySettingsFragmentVM.callPocketMoneyReminderDeleteData(
                DeletePocketMoneyReminder(
                    mobile = mobile
                )
            )
        }

        dialogDisableConfirm.tvCancelNotificationClick.setOnClickListener {
            dialogDisableConfirm.dismiss()
        }

        dialogDisableConfirm.show()
    }

    override fun onTryAgainClicked() {}

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_pocket_money_settings
    }

    override fun getViewModel(): PocketMoneySettingsFragmentVM = pocketMoneySettingsFragmentVM

}
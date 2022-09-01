package com.fypmoney.view.whatsappnoti.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentNotificationSettingsBinding
import com.fypmoney.util.Utility
import com.fypmoney.view.whatsappnoti.viewmodel.NotificationSettingsFragmentVM
import kotlinx.android.synthetic.main.dialog_settings_confirm.*
import kotlinx.android.synthetic.main.toolbar.*

class NotificationSettingsFragment :
    BaseFragment<FragmentNotificationSettingsBinding, NotificationSettingsFragmentVM>() {

    private lateinit var binding: FragmentNotificationSettingsBinding
    private val notificationSettingsVM by viewModels<NotificationSettingsFragmentVM> { defaultViewModelProviderFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = getViewDataBinding()

        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = "Notification Settings",
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )

        setObserver()

        binding.smWhatsAppNotification.setOnClickListener {
            if (binding.smWhatsAppNotification.isChecked) {
                binding.smWhatsAppNotification.isClickable = false
                notificationSettingsVM.callChangeOptStatusApi("OPT_IN")
            } else {
                binding.smWhatsAppNotification.isChecked = true
                binding.smWhatsAppNotification.isClickable = false
                callConfirmDisableNotificationDialog(requireContext())
            }
        }
    }

    private fun setObserver() {

        notificationSettingsVM.stateOptStatusChange.observe(viewLifecycleOwner) {
            handleState(it)
        }
    }

    private fun handleState(it: NotificationSettingsFragmentVM.WhatsAppNotificationOptState?) {
        when (it) {
            is NotificationSettingsFragmentVM.WhatsAppNotificationOptState.Success -> {
                Utility.showToast("Status: ${it.whatsAppOptData.isOptInDone}")
                binding.smWhatsAppNotification.isChecked =
                    it.whatsAppOptData.isOptInDone.equals("YES")
                binding.smWhatsAppNotification.isClickable = true
            }
            is NotificationSettingsFragmentVM.WhatsAppNotificationOptState.Error -> {
                if (it.errorResponseInfo.errorCode == "NTS_1008") {
                    binding.smWhatsAppNotification.isChecked = true
                } else {
                    Utility.showToast("We're encounter some issue at the moment. Please try again later...")
                    binding.smWhatsAppNotification.isChecked = false
                }
                binding.smWhatsAppNotification.isClickable = true
            }
            NotificationSettingsFragmentVM.WhatsAppNotificationOptState.Loading -> {}
            null -> {}
        }
    }

    private fun callConfirmDisableNotificationDialog(context: Context) {

        val dialogDisableConfirm = Dialog(context)

        dialogDisableConfirm.setCancelable(false)
        dialogDisableConfirm.setCanceledOnTouchOutside(false)
        dialogDisableConfirm.setContentView(R.layout.dialog_settings_confirm)

        val wlp = dialogDisableConfirm.window?.attributes
        wlp?.width = ViewGroup.LayoutParams.MATCH_PARENT
        dialogDisableConfirm.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogDisableConfirm.window?.attributes = wlp

        dialogDisableConfirm.btnDisableNotifications?.setOnClickListener {
            notificationSettingsVM.callChangeOptStatusApi("OPT_OUT")
            dialogDisableConfirm.dismiss()
        }

        dialogDisableConfirm.tvCancelNotificationClick.setOnClickListener {
            dialogDisableConfirm.dismiss()
            binding.smWhatsAppNotification.isClickable = true
        }

        dialogDisableConfirm.show()
    }

    override fun onTryAgainClicked() {}

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_notification_settings
    }

    override fun getViewModel(): NotificationSettingsFragmentVM = notificationSettingsVM

}
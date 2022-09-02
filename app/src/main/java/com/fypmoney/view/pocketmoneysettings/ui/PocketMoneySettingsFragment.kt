package com.fypmoney.view.pocketmoneysettings.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentPocketMoneySettingsBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.view.pocketmoneysettings.adapter.PocketMoneyReminderAdapter
import com.fypmoney.view.pocketmoneysettings.adapter.PocketMoneyReminderUiModel
import com.fypmoney.view.pocketmoneysettings.model.Data
import com.fypmoney.view.pocketmoneysettings.viewmodel.PocketMoneySettingsFragmentVM
import kotlinx.android.synthetic.main.toolbar.*

class PocketMoneySettingsFragment : BaseFragment<FragmentPocketMoneySettingsBinding, PocketMoneySettingsFragmentVM>() {

    private val pocketMoneySettingsFragmentVM by viewModels<PocketMoneySettingsFragmentVM> { defaultViewModelProviderFactory }
    private lateinit var binding: FragmentPocketMoneySettingsBinding

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

    public interface OnClickListener {
        fun onClick()
    }

    private fun openAddReminderBottomSheet() {
        val addReminderBottomSheet = AddNowPocketMoneyBottomSheet()
        addReminderBottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        addReminderBottomSheet.show(childFragmentManager, "AddNowPocketMoneyBottomSheet")
        addReminderBottomSheet.setOnActionCompleteListener(listener)
    }

    val listener = object: AddNowPocketMoneyBottomSheet.OnActionCompleteListener {
        override fun onActionComplete(data: Data?) {
            openOtpReminderBottomSheet(data)
        }
    }

    private val notifyListener = object : OtpReminderBottomSheet.OnActionCompleteListener{
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
        otpReminderBottomSheet.show(childFragmentManager, "OtpReminderBottomSheet")
        otpReminderBottomSheet.setOnActionCompleteListener(notifyListener)

    }

    private fun setObserver() {
        pocketMoneySettingsFragmentVM.stateReminderCoupon.observe(viewLifecycleOwner) {
            handleReminderCouponState(it)
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
            childFragmentManager,
            this.requireContext(),
            object : OnClickListener{
                override fun onClick() {
                    pocketMoneySettingsFragmentVM.callPocketMoneyReminderData()
                }

            })
        with(binding.rvPocketMoneyReminder) {
            adapter = pocketMoneyReminderAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
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
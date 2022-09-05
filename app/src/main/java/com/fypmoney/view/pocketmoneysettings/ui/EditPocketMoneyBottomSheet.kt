package com.fypmoney.view.pocketmoneysettings.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import com.fypmoney.R
import com.fypmoney.base.BaseBottomSheetFragment
import com.fypmoney.base.BaseViewModel
import com.fypmoney.databinding.BottomSheetSetupPocketMoneyBinding
import com.fypmoney.extension.bottomSheetTouchOutsideDisableOnly
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.model.SetPocketMoneyReminder
import com.fypmoney.util.Utility
import com.fypmoney.view.pocketmoneysettings.viewmodel.AddOrEditReminderViewModel
import kotlinx.android.synthetic.main.bottom_sheet_setup_pocket_money.*

class EditPocketMoneyBottomSheet : BaseBottomSheetFragment<BottomSheetSetupPocketMoneyBinding>() {

    private val addOrEditReminderViewModel by viewModels<AddOrEditReminderViewModel> { defaultViewModelProviderFactory }
    private var frequencyValue: String? = null

    private lateinit var editNotifyListener: OnEditReminderActionCompleteListener
    fun setOnEditReminderActionCompleteListener(editNotifyListener: OnEditReminderActionCompleteListener) {
        this.editNotifyListener = editNotifyListener
    }

    override val baseFragmentVM: BaseViewModel
        get() = addOrEditReminderViewModel
    override val customTag: String
        get() = EditPocketMoneyBottomSheet::class.java.simpleName
    override val layoutId: Int
        get() = R.layout.bottom_sheet_setup_pocket_money

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpBinding()

        dialog!!.window?.decorView?.findViewById<View>(R.id.touch_outside)?.bottomSheetTouchOutsideDisableOnly()

        setUpObserver()

        setViews()

        binding.cvDaily.setOnClickListener {
            frequencyValue = "DAILY"
            selectCard("DAILY")
            unSelectCard("Weekly")
            unSelectCard("Monthly")
        }

        binding.cvWeekly.setOnClickListener {
            frequencyValue = "WEEKLY"
            selectCard("WEEKLY")
            unSelectCard("Daily")
            unSelectCard("Monthly")
        }

        binding.cvMonthly.setOnClickListener {
            frequencyValue = "MONTHLY"
            selectCard("MONTHLY")
            unSelectCard("Weekly")
            unSelectCard("Daily")
        }

        setListeners()

        binding.buttonConfirmReminder.setOnClickListener {
            val name: String = etName.text.toString().trim()
            val number: String = etContactNumber.text.toString().trim()
            val amount: String = etPocketMoneyAmount.text.toString().trim()

            if (name.isEmpty())
                Utility.showToast("Please enter name")
            else if (etContactNumber.text.toString().trim().isEmpty())
                Utility.showToast("Please enter number")
            else if (etPocketMoneyAmount.text.toString().trim().isEmpty())
                Utility.showToast("Please enter amount")
            else if (etContactNumber.text?.length!! < 10)
                Utility.showToast("Please enter correct number")
            else if (etPocketMoneyAmount.text.toString().trim().toInt() < 10)
                Utility.showToast("Amount should be greater than ₹10")
            else if (etPocketMoneyAmount.text.toString().trim().toInt() > 5000)
                Utility.showToast("Amount should be less than ₹5000")
            else if (frequencyValue == null || frequencyValue.equals("null"))
                Utility.showToast("Please select allowance frequency")
            else
                addOrEditReminderViewModel.callPocketMoneySendOtp(
                    SetPocketMoneyReminder(
                        identifierType = "MOBILE",
                        mobile = number,
                        name = name,
                        amount = amount.toInt(),
                        frequency = frequencyValue,
                        relation = ""
                    )
                )
        }

    }

    private fun setUpObserver() {
        addOrEditReminderViewModel.stateReminderPocketMoney.observe(viewLifecycleOwner) {
            handleSendOtpState(it)
        }
    }

    private fun handleSendOtpState(pocketMoneyReminderState: AddOrEditReminderViewModel.PocketMoneyReminderState) {
        when (pocketMoneyReminderState) {
            is AddOrEditReminderViewModel.PocketMoneyReminderState.Error -> {

            }
            AddOrEditReminderViewModel.PocketMoneyReminderState.Loading -> {}
            is AddOrEditReminderViewModel.PocketMoneyReminderState.Success -> {
                editNotifyListener.onEditReminderActionComplete("done")
                Utility.showToast("Reminder edited successfully")
                dismiss()
            }
        }
    }

    private fun setViews() {
        binding.etContactNumber.isClickable = false
        binding.etContactNumber.isFocusable = false
        binding.etContactNumber.keyListener = null
        binding.etContactNumber.setTextColor(
            ContextCompat.getColor(
                binding.etContactNumber.context,
                R.color.text_grey
            )
        )
        binding.ivClipboardContact.toGone()
        binding.etName.setText(arguments?.getString("name"))
        binding.etContactNumber.setText(arguments?.getString("mobile"))
        binding.etPocketMoneyAmount.setText(arguments?.getInt("amount").toString())
        frequencyValue = arguments?.getString("frequency").toString()
        frequencyValue?.let { selectCard(it) }
    }

    private fun setListeners() {
        binding.etContactNumber.doOnTextChanged { text, _, _, _ ->
            if (binding.etContactNumber.text.toString().trim() == "0")
                binding.etContactNumber.text?.clear()

            if (!text.isNullOrEmpty() && text.length < 10) {
                binding.tvErrorMobileNumber.toVisible()
            } else {
                binding.tvErrorMobileNumber.toGone()
            }
        }
        binding.etPocketMoneyAmount.doOnTextChanged { _, _, _, _ ->
            if (binding.etPocketMoneyAmount.text.toString()
                    .trim().isNotEmpty() && binding.etPocketMoneyAmount.text.toString().trim()
                    .toInt() < 10
            ) {
                binding.tvErrorAmountExceed.toVisible()
                binding.tvErrorAmountExceed.text =
                    String.format("Amount should be greater than ₹10")
            } else if (binding.etPocketMoneyAmount.text.toString()
                    .trim().isNotEmpty() && binding.etPocketMoneyAmount.text.toString().trim()
                    .toInt() > 5000
            ) {
                binding.tvErrorAmountExceed.toVisible()
                binding.tvErrorAmountExceed.text = String.format("Amount should be less than ₹5000")
            } else
                binding.tvErrorAmountExceed.toGone()

        }
    }

    private fun selectCard(frequencyValue: String) {
        when (frequencyValue) {
            "DAILY" -> {
                binding.cvDaily.setCardBackgroundColor(
                    ContextCompat.getColor(
                        binding.cvDaily.context,
                        R.color.amount_bg2
                    )
                )
                TextViewCompat.setCompoundDrawableTintList(
                    binding.tvDaily,
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            binding.tvDaily.context,
                            R.color.back_surface_color
                        )
                    )
                )
                binding.tvDaily.setTextColor(
                    ContextCompat.getColor(
                        binding.tvDaily.context,
                        R.color.black_grey_txt_color
                    )
                )
            }
            "WEEKLY" -> {
                binding.cvWeekly.setCardBackgroundColor(
                    ContextCompat.getColor(
                        binding.cvWeekly.context,
                        R.color.amount_bg2
                    )
                )
                TextViewCompat.setCompoundDrawableTintList(
                    binding.tvWeekly,
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            binding.tvWeekly.context,
                            R.color.back_surface_color
                        )
                    )
                )
                binding.tvWeekly.setTextColor(
                    ContextCompat.getColor(
                        binding.tvWeekly.context,
                        R.color.black_grey_txt_color
                    )
                )
            }

            "MONTHLY" -> {
                binding.cvMonthly.setCardBackgroundColor(
                    ContextCompat.getColor(
                        binding.cvMonthly.context,
                        R.color.amount_bg2
                    )
                )
                TextViewCompat.setCompoundDrawableTintList(
                    binding.tvMonthly,
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            binding.tvMonthly.context,
                            R.color.back_surface_color
                        )
                    )
                )
                binding.tvMonthly.setTextColor(
                    ContextCompat.getColor(
                        binding.tvMonthly.context,
                        R.color.black_grey_txt_color
                    )
                )
            }
        }
    }

    private fun unSelectCard(frequencyValue: String) {
        when (frequencyValue) {
            "Daily" -> {
                binding.cvDaily.setCardBackgroundColor(
                    ContextCompat.getColor(
                        binding.cvDaily.context,
                        R.color.card_back_pocket
                    )
                )
                TextViewCompat.setCompoundDrawableTintList(
                    binding.tvDaily,
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            binding.tvDaily.context,
                            R.color.white
                        )
                    )
                )
                binding.tvDaily.setTextColor(
                    ContextCompat.getColor(
                        binding.tvDaily.context,
                        R.color.white
                    )
                )
            }
            "Weekly" -> {
                binding.cvWeekly.setCardBackgroundColor(
                    ContextCompat.getColor(
                        binding.cvWeekly.context,
                        R.color.card_back_pocket
                    )
                )
                TextViewCompat.setCompoundDrawableTintList(
                    binding.tvWeekly,
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            binding.tvWeekly.context,
                            R.color.white
                        )
                    )
                )
                binding.tvWeekly.setTextColor(
                    ContextCompat.getColor(
                        binding.tvWeekly.context,
                        R.color.white
                    )
                )
            }

            "Monthly" -> {
                binding.cvMonthly.setCardBackgroundColor(
                    ContextCompat.getColor(
                        binding.cvMonthly.context,
                        R.color.card_back_pocket
                    )
                )
                TextViewCompat.setCompoundDrawableTintList(
                    binding.tvMonthly,
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            binding.tvMonthly.context,
                            R.color.white
                        )
                    )
                )
                binding.tvMonthly.setTextColor(
                    ContextCompat.getColor(
                        binding.tvMonthly.context,
                        R.color.white
                    )
                )
            }
        }
    }

    interface OnEditReminderActionCompleteListener {
        fun onEditReminderActionComplete(data: String)
    }

    private fun setUpBinding() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
        }
    }
}
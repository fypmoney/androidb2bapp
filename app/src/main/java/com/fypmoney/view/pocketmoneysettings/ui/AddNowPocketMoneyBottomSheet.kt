package com.fypmoney.view.pocketmoneysettings.ui

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
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
import com.fypmoney.view.pocketmoneysettings.model.Data
import com.fypmoney.view.pocketmoneysettings.viewmodel.AddOrEditReminderViewModel
import kotlinx.android.synthetic.main.bottom_sheet_setup_pocket_money.*

class AddNowPocketMoneyBottomSheet : BaseBottomSheetFragment<BottomSheetSetupPocketMoneyBinding>() {

    private val addOrEditReminderViewModel by viewModels<AddOrEditReminderViewModel> { defaultViewModelProviderFactory }
    private var frequencyValue: String? = null

    private lateinit var otpListener: CloseAddBottomActionCompleteListener
    fun setOnAddReminderActionCompleteListener(otpListener: CloseAddBottomActionCompleteListener) {
        this.otpListener = otpListener
    }

    override val baseFragmentVM: BaseViewModel
        get() = addOrEditReminderViewModel
    override val customTag: String
        get() = AddNowPocketMoneyBottomSheet::class.java.simpleName
    override val layoutId: Int
        get() = R.layout.bottom_sheet_setup_pocket_money

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                when (val finalResult =
                    Utility.getPhoneNumberFromContact(requireActivity(), data)) {
                    is Utility.MobileNumberFromPhoneBook.MobileNumberFound -> {
                        binding.etContactNumber.setText(finalResult.phoneNumber)
                        finalResult.name?.let {
                            binding.etName.setText(it)
                        }
                    }
                    is Utility.MobileNumberFromPhoneBook.UnableToFindMobileNumber -> {
                        Utility.showToast(finalResult.errorMsg)
                    }
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpBinding()
        binding.viewModel = addOrEditReminderViewModel

        dialog!!.window?.decorView?.findViewById<View>(R.id.touch_outside)
            ?.bottomSheetTouchOutsideDisableOnly()

        setUpObserver()

        defaultCardSelect()

        binding.cvDaily.setOnClickListener {
            frequencyValue = "DAILY"
            selectCard("Daily")
            unSelectCard("Weekly")
            unSelectCard("Monthly")
        }

        binding.cvWeekly.setOnClickListener {
            frequencyValue = "WEEKLY"
            selectCard("Weekly")
            unSelectCard("Daily")
            unSelectCard("Monthly")
        }

        binding.cvMonthly.setOnClickListener {
            frequencyValue = "MONTHLY"
            selectCard("Monthly")
            unSelectCard("Weekly")
            unSelectCard("Daily")
        }

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
            else if (frequencyValue == null)
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

        addOrEditReminderViewModel.phoneBookEvent.observe(viewLifecycleOwner) {
            handleEvent(it)
        }

        addOrEditReminderViewModel.stateAmountPocketMoney.observe(viewLifecycleOwner) {
            handleEditTextState(it)
        }
    }

    private fun handleEditTextState(it: AddOrEditReminderViewModel.ReminderEditTextState?) {
        when (it) {
            AddOrEditReminderViewModel.ReminderEditTextState.GreaterThanFiveThousand -> {
                binding.tvErrorAmountExceed.toVisible()
                binding.tvErrorAmountExceed.text = String.format("Amount should be less than ₹5000")
            }
            AddOrEditReminderViewModel.ReminderEditTextState.LessThanTen -> {
                binding.tvErrorAmountExceed.toVisible()
                binding.tvErrorAmountExceed.text =
                    String.format("Amount should be greater than ₹10")
            }
            null -> {
            }
            AddOrEditReminderViewModel.ReminderEditTextState.GreaterThanTenLessThanFiveThousand -> {
                binding.tvErrorAmountExceed.toGone()
            }
            AddOrEditReminderViewModel.ReminderEditTextState.MobileNumberIsInvalid -> {
                binding.tvErrorMobileNumber.toVisible()
            }
            AddOrEditReminderViewModel.ReminderEditTextState.MobileNumberIsValid -> {
                binding.tvErrorMobileNumber.toGone()
            }
            AddOrEditReminderViewModel.ReminderEditTextState.MobileNumberZeroNotAllowed -> {
                binding.etContactNumber.text?.clear()
            }
        }
    }

    private fun handleEvent(it: AddOrEditReminderViewModel.AddReminderEvents?) {
        when (it) {
            AddOrEditReminderViewModel.AddReminderEvents.PickContactFromContactBookEvent -> {
                selectContactFromPhoneContactList()
            }
            null -> {}
        }
    }

    private fun handleSendOtpState(pocketMoneyReminderState: AddOrEditReminderViewModel.PocketMoneyReminderState) {
        when (pocketMoneyReminderState) {
            is AddOrEditReminderViewModel.PocketMoneyReminderState.Error -> {

            }
            AddOrEditReminderViewModel.PocketMoneyReminderState.Loading -> {}
            is AddOrEditReminderViewModel.PocketMoneyReminderState.Success -> {
                Utility.showToast("Otp Sent")
                otpListener.onOtpSuccessActionComplete(pocketMoneyReminderState.dataItem)
                dismiss()
            }
        }
    }

    private fun defaultCardSelect() {

        frequencyValue = "WEEKLY"

        selectCard("Weekly")
        unSelectCard("Daily")
        unSelectCard("Monthly")
    }

    private fun selectContactFromPhoneContactList() {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        resultLauncher.launch(intent)
    }

    private fun selectCard(frequencyValue: String) {
        when (frequencyValue) {
            "Daily" -> {
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
            "Weekly" -> {
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

            "Monthly" -> {
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

    interface CloseAddBottomActionCompleteListener {
        fun onOtpSuccessActionComplete(data: Data?)
    }

    private fun setUpBinding() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
        }
    }
}
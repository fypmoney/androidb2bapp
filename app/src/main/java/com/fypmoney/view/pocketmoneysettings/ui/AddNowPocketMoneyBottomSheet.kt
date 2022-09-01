package com.fypmoney.view.pocketmoneysettings.ui

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import com.fypmoney.R
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.databinding.BottomSheetSetupPocketMoneyBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.model.SetPocketMoneyReminder
import com.fypmoney.util.Utility
import com.fypmoney.view.pocketmoneysettings.model.Data
import com.fypmoney.view.pocketmoneysettings.model.PocketMoneyReminderResponse
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_setup_pocket_money.*

class AddNowPocketMoneyBottomSheet :
    BottomSheetDialogFragment(), WebApiCaller.OnWebApiResponse {

    private lateinit var binding: BottomSheetSetupPocketMoneyBinding
    private var frequencyValue: String? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.bottom_sheet_setup_pocket_money,
            container,
            false
        )

        binding.ivClipboardContact.setOnClickListener {
            selectContactFromPhoneContactList()
        }

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
            else if (frequencyValue == null)
                Utility.showToast("Please select allowance frequency")
            else
                addPocketMoneyReminder(
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

        return binding.root
    }

    private fun defaultCardSelect() {

        frequencyValue = "WEEKLY"

        selectCard("Weekly")
        unSelectCard("Daily")
        unSelectCard("Monthly")
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    private fun selectContactFromPhoneContactList() {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        resultLauncher.launch(intent)
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
            if (!binding.etPocketMoneyAmount.text.toString()
                    .trim().isNullOrEmpty() && binding.etPocketMoneyAmount.text.toString().trim()
                    .toInt() < 10
            ) {
                binding.tvErrorAmountExceed.toVisible()
                binding.tvErrorAmountExceed.text = "Amount should be greater than ₹10"
            } else if (!binding.etPocketMoneyAmount.text.toString()
                    .trim().isNullOrEmpty() && binding.etPocketMoneyAmount.text.toString().trim()
                    .toInt() > 5000
            ) {
                binding.tvErrorAmountExceed.toVisible()
                binding.tvErrorAmountExceed.text = "Amount should be less than ₹5000"
            } else
                binding.tvErrorAmountExceed.toGone()

        }
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

    val listener = object: OtpReminderBottomSheet.OnActionCompleteListener {
        override fun onActionComplete(str: String) {
            // do what you want to do here because this block will be invoked in bottom sheet
            // you will receive "your password is this" here
            // as per question maybe check your password / pin
            dismiss()
        }
    }

    private fun addPocketMoneyReminder(setPocketMoneyReminder: SetPocketMoneyReminder) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_ADD_POCKET_MONEY_REMINDER,
                NetworkUtil.endURL(ApiConstant.API_ADD_POCKET_MONEY_REMINDER),
                ApiUrl.POST,
                setPocketMoneyReminder,
                this, isProgressBar = true
            )
        )
    }

    override fun progress(isStart: Boolean, message: String) {
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        when (purpose) {
            ApiConstant.API_ADD_POCKET_MONEY_REMINDER -> {
                Utility.showToast(responseData.toString())
                if (responseData is PocketMoneyReminderResponse) {
                    Utility.showToast("Otp Sent")
                    openAddReminderBottomSheet(responseData.data)
                }
            }
        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        when (purpose) {
            ApiConstant.API_ADD_POCKET_MONEY_REMINDER -> {
                Utility.showToast(errorResponseInfo.msg)
            }
        }
    }

    private fun openAddReminderBottomSheet(data: Data?) {
        val otpReminderBottomSheet = OtpReminderBottomSheet()
        val bundle = Bundle()
        bundle.putString("otpIdentifier", data?.otpIdentifier)
        bundle.putString("name", data?.name)
        bundle.putString("mobile", data?.mobile)
        data?.amount?.let { bundle.putInt("amount", it) }
        bundle.putString("frequency", data?.frequency)
        otpReminderBottomSheet.setOnActionCompleteListener(listener)
        otpReminderBottomSheet.arguments = bundle
        otpReminderBottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        otpReminderBottomSheet.show(childFragmentManager, "OtpReminderBottomSheet")

    }

    override fun offLine() {
    }

}
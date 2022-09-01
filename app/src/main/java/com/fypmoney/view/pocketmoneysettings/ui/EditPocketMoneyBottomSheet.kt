package com.fypmoney.view.pocketmoneysettings.ui

import android.app.Dialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.fypmoney.view.pocketmoneysettings.model.PocketMoneyReminderResponse
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_setup_pocket_money.*

class EditPocketMoneyBottomSheet : BottomSheetDialogFragment(), WebApiCaller.OnWebApiResponse{

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
                addPocketMoneyReminderData(
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val touchOutsideView = dialog!!.window
            ?.decorView
            ?.findViewById<View>(R.id.touch_outside)
        touchOutsideView?.setOnClickListener(null)
    }

    private fun setViews() {
        binding.etContactNumber.isClickable = false
        binding.etContactNumber.isFocusable = false
        binding.etContactNumber.keyListener = null
        binding.etContactNumber.setTextColor(ContextCompat.getColor(
            binding.etContactNumber.context,
            R.color.text_grey
        ))
        binding.ivClipboardContact.toGone()
        binding.etName.setText(arguments?.getString("name"))
        binding.etContactNumber.setText(arguments?.getString("mobile"))
        binding.etPocketMoneyAmount.setText(arguments?.getInt("amount").toString())
        frequencyValue = arguments?.getString("frequency").toString()
        frequencyValue?.let { selectCard(it) }
    }

    private fun setListeners() {
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

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    private fun addPocketMoneyReminderData(setPocketMoneyReminder: SetPocketMoneyReminder) {
        addPocketMoneyReminder(setPocketMoneyReminder)
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

    override fun progress(isStart: Boolean, message: String) {}

    override fun onSuccess(purpose: String, responseData: Any) {
        when (purpose) {
            ApiConstant.API_ADD_POCKET_MONEY_REMINDER -> {
                if (responseData is PocketMoneyReminderResponse) {
                    dismiss()
                    Utility.showToast("Reminder edited successfully")
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

    override fun offLine() {}
}
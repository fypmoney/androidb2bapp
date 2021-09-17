package com.fypmoney.view.fragment


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.databinding.BottomSheetActivateCardBinding
import com.fypmoney.databinding.BottomSheetSetSpendingLimitBinding
import com.fypmoney.util.Utility
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mukesh.OtpView
import kotlinx.android.synthetic.main.view_enter_otp.*


class ActivateCardBottomSheet(var onActivateCardClickListener: OnActivateCardClickListener) :
    BottomSheetDialogFragment() {
    lateinit var  binding: BottomSheetActivateCardBinding
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    var otp = ObservableField<String>()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.bottom_sheet_activate_card,
            container,
            false
        )


        binding.tNCCb.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                binding.buttonOtp.isEnabled = true
            }
        }
        binding.buttonOtp.setOnClickListener {
            when {
                TextUtils.isEmpty(otp.get()) -> {
                    Utility.showToast(PockketApplication.instance.getString(R.string.card_kit_empty_error))
                }
                else -> {
                    onActivateCardClickListener.onActivateCardClick(otp.get())
                    dismiss()
                }
            }


        }
        binding.otpView.setOtpCompletionListener { otp1 -> // do Stuff
            otp.set(otp1)
        }

        return binding.root
    }

    interface OnActivateCardClickListener {
        fun onActivateCardClick(kitFourDigit: String?)

    }


}
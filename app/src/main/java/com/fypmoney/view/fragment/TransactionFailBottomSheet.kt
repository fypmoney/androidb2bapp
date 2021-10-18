package com.fypmoney.view.fragment


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.TrackrField
import com.fyp.trackr.models.trackr
import com.fyp.trackr.services.TrackrServices
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.databinding.BottomSheetTransactionFailBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


/*
* This is used to show transaction fail in case of add money
* */
class TransactionFailBottomSheet(
    private val fromWhichScreen: String,
    private val amount1: String, private var onBottomSheetClickListener: OnBottomSheetClickListener
) : BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.bottom_sheet_transaction_fail,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val bottomSheet = BottomSheetDialog(requireContext())
        bottomSheet.setCancelable(false)
        val bindingSheet = DataBindingUtil.inflate<BottomSheetTransactionFailBinding>(
            layoutInflater,
            R.layout.bottom_sheet_transaction_fail,
            null,
            false
        )
        bottomSheet.setContentView(bindingSheet.root)

        val amount = view.findViewById<TextView>(R.id.add_amount)!!
        val changeMethod = view.findViewById<TextView>(R.id.changeMethod)!!
        val linearLayout = view.findViewById<LinearLayout>(R.id.linear_layout)!!
        val retry = view.findViewById<Button>(R.id.button_retry)!!
        val buttonRetryInCenter = view.findViewById<Button>(R.id.button_retry_InCenter)!!

        amount.text =
            getString(R.string.add_money_trans_fail_add) + getString(R.string.Rs) + amount1

        when (fromWhichScreen) {
            AppConstants.PAY -> {
                linearLayout.visibility = View.GONE
                buttonRetryInCenter.visibility = View.VISIBLE

            }
            else -> {
            }
        }
        buttonRetryInCenter.setOnClickListener {
            dismiss()
        }

        changeMethod.setOnClickListener {
            dismiss()
        }
        retry.setOnClickListener {
            onBottomSheetClickListener.onBottomSheetButtonClick("")
            dismiss()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        trackr { it.services = arrayListOf(TrackrServices.MOENGAGE)
            it.name = TrackrEvent.LOADMONEYFAIL
            it.add(
                TrackrField.user_mobile_no, SharedPrefUtils.getString(PockketApplication.instance,
                SharedPrefUtils.SF_KEY_USER_MOBILE))
            it.add(TrackrField.transaction_amount,amount1)
        }
    }
    interface OnBottomSheetClickListener {
        fun onBottomSheetButtonClick(type: String)

    }


}
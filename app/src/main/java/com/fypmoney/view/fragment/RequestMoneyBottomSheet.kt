package com.fypmoney.view.fragment


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.fypmoney.R
import com.fypmoney.databinding.BottomSheetFamilyNotificationBinding
import com.fypmoney.databinding.BottomSheetRequestMoneyBinding
import com.fypmoney.model.NotificationModel
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_request_money.*
import org.json.JSONObject


/*
* This is used to show request money details in case of add money
* */
class RequestMoneyBottomSheet(
    private val response: NotificationModel.NotificationResponseDetails,
    private var onBottomSheetClickListener: OnRequestMoneyBottomSheetClickListener
) : BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.bottom_sheet_request_money,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val bottomSheet = BottomSheetDialog(requireContext())
        val bindingSheet = DataBindingUtil.inflate<BottomSheetRequestMoneyBinding>(
            layoutInflater,
            R.layout.bottom_sheet_request_money,
            null,
            false
        )
        bottomSheet.setContentView(bindingSheet.root)
        val description = view.findViewById<TextView>(R.id.description)!!
        val title = view.findViewById<TextView>(R.id.verification_title)!!
        val name = view.findViewById<TextView>(R.id.name)!!
        val phone = view.findViewById<TextView>(R.id.number)!!
        val amount = view.findViewById<TextView>(R.id.amount)!!
        val btnReject = view.findViewById<TextView>(R.id.button_reject)!!
        val btnAccept = view.findViewById<TextView>(R.id.button_accept)!!
        val btnCancel = view.findViewById<TextView>(R.id.button_cancel)!!

        when (response.isApprovalProcessed) {
            AppConstants.YES -> {
                btnReject.visibility = View.GONE
                btnAccept.visibility = View.GONE
                btnCancel.visibility = View.GONE
                object : CountDownTimer(10000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                    }

                    override fun onFinish() {
                        dismiss()
                    }
                }.start()

            }
        }

        val actionAllowedList = response.actionAllowed?.split(",")
        if (actionAllowedList?.size!! > 1) {
            btnCancel.visibility = View.GONE
            title.text = getString(R.string.request_money_by)


        } else {
            btnAccept.visibility = View.GONE
            btnReject.visibility = View.GONE
            title.text = getString(R.string.request_money_to)

        }
        // get number , amount

        val mainObject = JSONObject(response.objectJson)


        description.text = response.description
        name.text = response.destinationUserName
      //  number.text = mainObject.getString("requesteeMobile")
        amount.text =
            getString(R.string.Rs) + Utility.getFormatedAmount(
                Utility.convertToRs(
                    mainObject.getString(
                        "amount"
                    )
                )
            )

        btnReject.setOnClickListener {
            onBottomSheetClickListener.onRequestMoneyBottomSheetButtonClick(actionAllowedList[0])
            dismiss()
        }
        btnAccept.setOnClickListener {
            onBottomSheetClickListener.onRequestMoneyBottomSheetButtonClick(actionAllowedList[1])
            dismiss()
        }

        btnCancel.setOnClickListener {
            onBottomSheetClickListener.onRequestMoneyBottomSheetButtonClick(response.actionAllowed)
            dismiss()
        }
        return view
    }

    interface OnRequestMoneyBottomSheetClickListener {
        fun onRequestMoneyBottomSheetButtonClick(actionAllowed: String?)

    }

}
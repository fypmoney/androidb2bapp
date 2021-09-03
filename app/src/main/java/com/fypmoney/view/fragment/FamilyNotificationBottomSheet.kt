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
import com.fypmoney.model.NotificationModel
import com.fypmoney.util.AppConstants
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


/*
* This is used to show transaction fail in case of add money
* */
class FamilyNotificationBottomSheet(
    private val actionAllowed: String?,
    private val descriptionValue: String?, private val isApprovalProcess: String? = null,
    var notificationResponse: NotificationModel.NotificationResponseDetails?,
    private var onBottomSheetClickListener: OnBottomSheetClickListener
) : BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.bottom_sheet_family_notification,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val bottomSheet = BottomSheetDialog(requireContext())
        val bindingSheet = DataBindingUtil.inflate<BottomSheetFamilyNotificationBinding>(
            layoutInflater,
            R.layout.bottom_sheet_family_notification,
            null,
            false
        )
        bottomSheet.setContentView(bindingSheet.root)

        val description = view.findViewById<TextView>(R.id.description)!!
        val btnReject = view.findViewById<TextView>(R.id.button_reject)!!
        val btnAccept = view.findViewById<TextView>(R.id.button_accept)!!
        val btnCancel = view.findViewById<TextView>(R.id.button_cancel)!!



        val actionAllowedList = actionAllowed?.split(",")
        if (actionAllowedList?.size!! > 1) {
            btnCancel.visibility = View.GONE
            btnAccept.visibility = View.VISIBLE
            btnReject.visibility = View.VISIBLE
        } else {
            btnAccept.visibility = View.GONE
            btnReject.visibility = View.GONE
            btnCancel.visibility = View.VISIBLE
        }


        description.text = descriptionValue
        when (isApprovalProcess) {
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
        btnReject.setOnClickListener {
            onBottomSheetClickListener.onBottomSheetButtonClick(actionAllowedList[0])
            dismiss()
        }
        btnAccept.setOnClickListener {
            onBottomSheetClickListener.onBottomSheetButtonClick(actionAllowedList[1])
            dismiss()
        }

        btnCancel.setOnClickListener {
            onBottomSheetClickListener.onBottomSheetButtonClick(actionAllowed)
            dismiss()
        }
        return view
    }

    interface OnBottomSheetClickListener {
        fun onBottomSheetButtonClick(actionAllowed: String?)

    }

}
package com.fypmoney.view.fragment


import android.app.Dialog
import android.content.ClipboardManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


/*
* This is used to update family name
* */
class InviteBottomSheet(
    var clipboardManager: ClipboardManager,
    var onShareClickListener: OnShareClickListener
) : BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.bottom_sheet_invite,
            container,
            false
        )

        val referralCode = view.findViewById<TextView>(R.id.title1)!!
        val referralMsg = view.findViewById<TextView>(R.id.title3)!!
        val shareButton = view.findViewById<Button>(R.id.button_share)!!

        referralCode.text = Utility.getCustomerDataFromPreference()?.referralCode
        var referralMsgValue: String? = ""
        if (Utility.getCustomerDataFromPreference()?.postKycScreenCode != null
            && Utility.getCustomerDataFromPreference()?.postKycScreenCode == "0") {
            referralMsgValue = if (!SharedPrefUtils.getString(
                    PockketApplication.instance,
                    SharedPrefUtils.SF_KEY_REFER_LINE2
                ).isNullOrEmpty()
            ) {
                SharedPrefUtils.getString(
                    PockketApplication.instance,
                    SharedPrefUtils.SF_KEY_REFER_LINE2)

            } else {
                ""
            }

        }

        referralMsg.text = referralMsgValue

        referralCode.setOnClickListener {
            Utility.copyTextToClipBoard(
                clipboardManager,
                Utility.getCustomerDataFromPreference()?.referralCode
            )
        }




        shareButton.setOnClickListener {
            onShareClickListener.onShareClickListener(referralCode.text.toString())
            dismiss()
        }


        return view
    }

    interface OnShareClickListener {
        fun onShareClickListener(referralCode: String)

    }

}
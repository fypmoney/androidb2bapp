package com.fypmoney.view.discord


import android.app.Activity
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
import com.fypmoney.databinding.BottomSheetDicordFailBinding

import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


/*
* This is used to show transaction fail in case of add money
* */
class DiscordBottomSheet(val discordWebView: Activity) : BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.bottom_sheet_dicord_fail,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val bottomSheet = BottomSheetDialog(requireContext())
        bottomSheet.setCancelable(false)
        val bindingSheet = DataBindingUtil.inflate<BottomSheetDicordFailBinding>(
            layoutInflater,
            R.layout.bottom_sheet_dicord_fail,
            null,
            false
        )
        bottomSheet.setContentView(bindingSheet.root)

        val changeMethod = view.findViewById<TextView>(R.id.changeMethod)!!



        changeMethod.setOnClickListener {
            dismiss()
            discordWebView.finish()

        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


}
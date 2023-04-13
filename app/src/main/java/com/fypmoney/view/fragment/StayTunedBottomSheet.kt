package com.fypmoney.view.fragment


import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.fypmoney.R
import com.fypmoney.databinding.ViewStayTunedBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.view.home.main.homescreen.view.HomeActivity
import com.fypmoney.view.kycagent.ui.KycAgentActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/*
* This is used to success message in case add member
* */
class StayTunedBottomSheet : BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.view_stay_tuned,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val bottomSheet = BottomSheetDialog(requireContext())
        val bindingSheet = DataBindingUtil.inflate<ViewStayTunedBinding>(
            layoutInflater,
            R.layout.view_stay_tuned,
            null,
            false
        )
        bottomSheet.setContentView(bindingSheet.root)
        bottomSheet.setCanceledOnTouchOutside(false)

        val stayTuned = view.findViewById<TextView>(R.id.stayTuned)!!
        val continueButton = view.findViewById<Button>(R.id.btnContinue)!!
        stayTuned.text =
            getString(R.string.stay_tuned_screen_sub_title)

        continueButton.setOnClickListener {
            navigateToDifferentActivity()

        }


        return view
    }

    /*
* navigate to the HomeScreen
* */
    private fun navigateToDifferentActivity() {
        val intent = Intent(context, KycAgentActivity::class.java)
        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, "stay_tuned")
        startActivity(intent)
        dismiss()

    }


}
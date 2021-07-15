package com.fypmoney.view.fragment


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.databinding.DataBindingUtil
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.databinding.BottomSheetCardSettingsBinding
import com.fypmoney.databinding.BottomSheetPriceBreakupBinding
import com.fypmoney.view.adapter.MyProfileListAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.screen_card.*


/*
* This is used to show price Breakup
* */
class PriceBreakupBottomSheet :
    BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.bottom_sheet_price_breakup,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val bottomSheet = BottomSheetDialog(requireContext())
        val bindingSheet = DataBindingUtil.inflate<BottomSheetPriceBreakupBinding>(
            layoutInflater,
            R.layout.bottom_sheet_price_breakup,
            null,
            false
        )

        bottomSheet.setContentView(bindingSheet.root)

        return view
    }


}
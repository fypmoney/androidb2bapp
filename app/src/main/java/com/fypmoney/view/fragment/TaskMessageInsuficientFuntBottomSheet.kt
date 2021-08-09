package com.fypmoney.view.fragment


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import com.fypmoney.R
import com.fypmoney.databinding.BottomSheetInsuficientFundBinding
import com.fypmoney.view.interfaces.AcceptRejectClickListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_response_task.*
import kotlinx.android.synthetic.main.bottom_sheet_response_task.view.*


class TaskMessageInsuficientFuntBottomSheet(
    var onClickListener: AcceptRejectClickListener,

    ) :
    BottomSheetDialogFragment() {
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    var otp = ObservableField<String>()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.bottom_sheet_insuficient_fund,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val bottomSheet = BottomSheetDialog(requireContext())
        val bindingSheet = DataBindingUtil.inflate<BottomSheetInsuficientFundBinding>(
            layoutInflater,
            R.layout.bottom_sheet_insuficient_fund,
            null,
            false
        )
        bottomSheet.setContentView(bindingSheet.root)

        val accept = view.findViewById<Button>(R.id.accept)!!
        accept.setOnClickListener(View.OnClickListener {

            onClickListener.onRejectClicked(0)

        })
        view.reject.setOnClickListener(View.OnClickListener {
            onClickListener.onAcceptClicked(
                0, comment.text?.trim()
                    .toString()
            )

        })






        return view
    }


}
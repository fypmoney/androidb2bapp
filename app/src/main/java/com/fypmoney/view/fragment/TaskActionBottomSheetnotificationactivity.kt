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
import com.fypmoney.databinding.BottomSheetResponseTaskBinding
import com.fypmoney.view.interfaces.AcceptRejectClickListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_response_task.view.*


class TaskActionBottomSheetnotificationactivity(
    var onClickListener: AcceptRejectClickListener,
    var list: String?
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
            R.layout.bottom_sheet_response_task,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val bottomSheet = BottomSheetDialog(requireContext())
        val bindingSheet = DataBindingUtil.inflate<BottomSheetResponseTaskBinding>(
            layoutInflater,
            R.layout.bottom_sheet_response_task,
            null,
            false
        )
        bottomSheet.setContentView(bindingSheet.root)


        val btnOtp = view.findViewById<Button>(R.id.accept)!!
        btnOtp.setOnClickListener(View.OnClickListener {
            onClickListener.onAcceptClicked(0)
        })
        view.reject.setOnClickListener(View.OnClickListener {
            onClickListener.onRejectClicked(0)
        })
//        view.amount.text="₹"+list.additionalAttributes?.amount
//        view.days_left.text=list.additionalAttributes?.numberOfDays.toString()+" days"
//        view.descrip.text=list.additionalAttributes?.description
//        view.verification_title.text=list.additionalAttributes?.title

        return view
    }


}
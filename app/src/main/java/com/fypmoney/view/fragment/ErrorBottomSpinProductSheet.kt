package com.fypmoney.view.fragment


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.fypmoney.R
import com.fypmoney.databinding.BottomSheetErrorBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.viewmodel.SpinWheelProductViewModel
import com.fypmoney.viewmodel.SpinWheelViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/*
* This is used to show the error in spin wheel
* */
class ErrorBottomSpinProductSheet(
    private var type: String, private var message: String?,
    private var onSpinErrorClickListener: OnSpinErrorClickListener,
    var mViewModel: SpinWheelProductViewModel,

    ) : BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.bottom_sheet_error,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val bottomSheet = BottomSheetDialog(requireContext())
        val bindingSheet = DataBindingUtil.inflate<BottomSheetErrorBinding>(
            layoutInflater,
            R.layout.bottom_sheet_error,
            null,
            false
        )
        bottomSheet.setContentView(bindingSheet.root)
        dialog?.setCancelable(false)

        val got = view.findViewById<Button>(R.id.button_logout)!!
        val image = view.findViewById<ImageView>(R.id.image)!!
        val text = view.findViewById<TextView>(R.id.text)!!
        val heading = view.findViewById<TextView>(R.id.verification_title)!!

        when (type) {
            AppConstants.ERROR_TYPE_SPIN_ALLOWED -> {
                text.text = getString(R.string.oops)
                heading.text = getString(R.string.oop)
                mViewModel.coinVisibilty.set(true)
                mViewModel.spinnerClickable.set(true)

            }
            else -> {
                heading.text = getString(R.string.oo_error)
                mViewModel.coinVisibilty.set(true)

            }
        }

        got.setOnClickListener {
            onSpinErrorClickListener.onSpinErrorClick(type)
            dialog!!.cancel()
        }



        return view
    }

    interface OnSpinErrorClickListener {
        fun onSpinErrorClick(type: String)

    }

}
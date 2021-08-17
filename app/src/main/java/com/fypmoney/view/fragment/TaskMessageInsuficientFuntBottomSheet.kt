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
import kotlinx.android.synthetic.main.bottom_sheet_insuficient_fund.*
import kotlinx.android.synthetic.main.bottom_sheet_response_task.*
import kotlinx.android.synthetic.main.bottom_sheet_response_task.view.*


class TaskMessageInsuficientFuntBottomSheet(
    var onClickListener: AcceptRejectClickListener,
    var title:String? = null,
    var subTitle:String? = null,
    var amount:String? = null
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

        val accept = view.findViewById<Button>(R.id.add_money_btn)!!
        val reject = view.findViewById<Button>(R.id.reject)!!
        accept.setOnClickListener {
            onClickListener.onRejectClicked(0)
        }
        reject.setOnClickListener {
            onClickListener.onAcceptClicked(
                0, ""
            )
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

            title?.let {
                card_title.text = title
            }

        subTitle?.let {
                sub_title_tv.text = it
            }

        if(amount.isNullOrEmpty()){
            add_money_tv.visibility = View.GONE
        }else{
            add_money_tv.text = amount
        }



    }
}
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
    var amount:String? = null,
    var background:String? = null,
    var titleColor:String? = null,
    var moneyTextColor:String? = null,
    var buttonColor:String? = null,
    ) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetInsuficientFundBinding
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.bottom_sheet_insuficient_fund,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        background?.let {
            binding.llMain.setBackgroundColor(Color.parseColor(it))
        }
        titleColor?.let {
            binding.cardTitle.setTextColor(Color.parseColor(it))
        }
        moneyTextColor?.let {
            binding.addMoneyTv.setTextColor(Color.parseColor(it))
        }
        buttonColor?.let {
            binding.addMoneyBtn.setBackgroundColor(Color.parseColor(it))
        }
        title?.let {
                binding.cardTitle.text = title
            }

        subTitle?.let {
                binding.subTitleTv.text = it
            }

        if(amount.isNullOrEmpty()){
            add_money_tv.visibility = View.GONE
        }else{
            add_money_tv.text = amount
        }


        binding.addMoneyBtn.setOnClickListener {
            onClickListener.onRejectClicked(0)
        }
        binding.reject.setOnClickListener {
            onClickListener.onAcceptClicked(
                0, ""
            )
        }


    }
}
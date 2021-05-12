package com.fypmoney.view.fragment


import android.app.Dialog
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
import com.fypmoney.database.entity.MemberEntity
import com.fypmoney.databinding.BottomSheetLeaveMemberBinding
import com.fypmoney.util.AppConstants
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_leave_member.*

/*
* This is used to leave or remove any member
* */
class LeaveMemberBottomSheetFragment(
    private var memberEntity: MemberEntity?,
    private val type: String, private var onBottomSheetClickListener: OnBottomSheetClickListener
) : BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.bottom_sheet_leave_member,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val bottomSheet = BottomSheetDialog(requireContext())
        val bindingSheet = DataBindingUtil.inflate<BottomSheetLeaveMemberBinding>(
            layoutInflater,
            R.layout.bottom_sheet_leave_member,
            null,
            false
        )
        bottomSheet.setContentView(bindingSheet.root)

        val title = view.findViewById<TextView>(R.id.title)!!
        val name = view.findViewById<TextView>(R.id.name)!!
        val cancel = view.findViewById<Button>(R.id.button_cancel)!!
        val leaveRemove = view.findViewById<Button>(R.id.button_leave)!!

        cancel.setOnClickListener {
            dialog!!.cancel()
        }
        leaveRemove.setOnClickListener {
            onBottomSheetClickListener.onBottomSheetButtonClick(type)
            dialog!!.cancel()
        }

        when (type) {
            AppConstants.LEAVE_MEMBER -> {
                name.visibility = View.GONE


            }
            AppConstants.REMOVE_MEMBER -> {
                name.text = memberEntity?.name
                when (memberEntity?.status) {
                    AppConstants.ADD_MEMBER_STATUS_APPROVED -> {
                        title.text = getString(R.string.remove_title)
                        leaveRemove.text = getString(R.string.remove_btn_text)
                    }
                    AppConstants.ADD_MEMBER_STATUS_INVITED -> {
                        title.text = getString(R.string.remove_title)
                        leaveRemove.text = getString(R.string.remove_btn_text)
                    }


                }


            }
        }

        return view
    }

    interface OnBottomSheetClickListener {
        fun onBottomSheetButtonClick(type: String)

    }

}
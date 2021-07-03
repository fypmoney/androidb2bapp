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
import com.fypmoney.base.BaseActivity
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.databinding.ViewInviteMemberBinding
import com.fypmoney.databinding.ViewStayTunedBinding
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.activity.AddMemberView
import com.fypmoney.view.activity.FamilySettingsView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/*
* This is used to invite a family member
* */
class InviteMemberBottomSheet(
    var type: String,
    var personName: String? = null,
    var onInviteButtonClickListener: OnInviteButtonClickListener
) :
    BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.view_invite_member,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val bottomSheet = BottomSheetDialog(requireContext())
        val bindingSheet = DataBindingUtil.inflate<ViewInviteMemberBinding>(
            layoutInflater,
            R.layout.view_invite_member,
            null,
            false
        )
        bottomSheet.setContentView(bindingSheet.root)

        val message = view.findViewById<TextView>(R.id.message)!!
        val inviteBtn = view.findViewById<Button>(R.id.inviteBtn)!!
        when {
            type != ApiConstant.API_CHECK_IS_APP_USER -> {
                message.text = type
                inviteBtn.visibility = View.GONE
            }
            else -> {
                if (personName.isNullOrEmpty()) {
                    message.text =
                        getString(R.string.invite_member_screen_sub_title) + SharedPrefUtils.getString(
                            requireContext(),
                            SharedPrefUtils.SF_KEY_SELECTED_RELATION
                        ) + getString(R.string.invite_member_screen_sub_title1)
                } else {
                    message.text =
                        getString(R.string.invite_member_screen_sub_title2) + personName + getString(
                            R.string.invite_member_screen_sub_title1
                        )
                }
            }

        }

        inviteBtn.setOnClickListener {
            onInviteButtonClickListener.onInviteButtonClick()
            dismiss()
        }


        return view
    }

    interface OnInviteButtonClickListener {
        fun onInviteButtonClick()
    }


}
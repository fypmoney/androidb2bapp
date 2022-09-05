package com.fypmoney.base

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import com.fypmoney.R
import com.fypmoney.util.DialogUtils
import com.fypmoney.util.autoCleared
import com.fypmoney.view.activity.LoginView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheetFragment <VDB : ViewDataBinding> : BottomSheetDialogFragment() {

    protected var binding by autoCleared<VDB>()
    abstract val baseFragmentVM: BaseViewModel
    abstract val customTag: String

    @get:LayoutRes
    abstract val layoutId: Int

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return DataBindingUtil.inflate<VDB>(inflater, layoutId, container, false).also {
            binding = it
        }.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), theme).apply {
            behavior.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO
            behavior.state = BottomSheetBehavior.STATE_EXPANDED

        }
        return bottomSheetDialog
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeForError()
    }

    private fun observeForError() {
        baseFragmentVM.logoutUser.observe(viewLifecycleOwner) {
            navigateToLogin()
        }

        baseFragmentVM.progressDialog.observe(viewLifecycleOwner)
        {
            when (it) {
                true -> {
                    DialogUtils.showProgressDialog(requireActivity())
                }
                false -> {
                    DialogUtils.dismissProgressDialog()
                }
            }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(requireContext(), LoginView::class.java)
        startActivity(intent)
        requireActivity().finishAffinity()
    }
}
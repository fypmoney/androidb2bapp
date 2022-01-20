package com.fypmoney.view.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.fypmoney.R
import com.fypmoney.base.BaseDialogFragment
import com.fypmoney.base.BaseViewModel
import com.fypmoney.databinding.DialogMaxLoadAmountReachedWarningBinding

class MaxLoadAmountReachedWarningDialogFragment:BaseDialogFragment<DialogMaxLoadAmountReachedWarningBinding>() {
    private val maxLoadAmountReachedWarningDialogFragmentVM by viewModels<MaxLoadAmountReachedWarningDialogFragmentVM>
    { defaultViewModelProviderFactory }
    override val baseFragmentVM: BaseViewModel
        get() = maxLoadAmountReachedWarningDialogFragmentVM
    override val customTag: String
        get() = MaxLoadAmountReachedWarningDialogFragment::class.java.simpleName
    override val layoutId: Int
        get() = R.layout.dialog_max_load_amount_reached_warning

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        handleObserver()
    }

    private fun handleObserver() {
        maxLoadAmountReachedWarningDialogFragmentVM.event.observe(viewLifecycleOwner,{
            when(it){
                MaxLoadAmountReachedWarningDialogFragmentVM.MaxLoadAmountReachedWarningDialogEvent.OkEvent -> {
                    dismiss()
                }
            }
        })
    }

    private fun setupViews() {
        with(binding){
            lifecycleOwner = viewLifecycleOwner
            viewModel = maxLoadAmountReachedWarningDialogFragmentVM
        }
    }
}
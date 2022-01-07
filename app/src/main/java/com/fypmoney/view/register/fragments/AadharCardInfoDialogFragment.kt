package com.fypmoney.view.register.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.fypmoney.R
import com.fypmoney.base.BaseDialogFragment
import com.fypmoney.base.BaseViewModel
import com.fypmoney.databinding.DialogFragmentAadharCardInfoBinding

class AadharCardInfoDialogFragment(): BaseDialogFragment<DialogFragmentAadharCardInfoBinding>(){
    private val aadharCardInfoDialogFragmentVM by viewModels<AadharCardInfoDialogFragmentVM> { defaultViewModelProviderFactory }
    override val baseFragmentVM: BaseViewModel
        get() = aadharCardInfoDialogFragmentVM
    override val customTag: String
        get() = AadharCardInfoDialogFragment::class.java.simpleName
    override val layoutId: Int
        get() = R.layout.dialog_fragment_aadhar_card_info

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        handelObserver()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        widthHeightFull = true
    }

    private fun handelObserver() {
        handelEvent()
    }

    private fun handelEvent() {
        aadharCardInfoDialogFragmentVM.event.observe(viewLifecycleOwner,{
            when(it){
                AadharCardInfoDialogFragmentVM.AadharCardInfoDialogEvent.OnCloseClick -> {
                    dismiss()
                }
            }
        })
    }

    private fun setupViews() {
        with(binding){
            lifecycleOwner = viewLifecycleOwner
            viewModel = aadharCardInfoDialogFragmentVM
        }
    }

}
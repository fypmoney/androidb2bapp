package com.fypmoney.view.setpindialog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.R
import com.fypmoney.base.BaseDialogFragment
import com.fypmoney.base.BaseViewModel
import com.fypmoney.databinding.FragmentSetPinDialogBinding


class SetPinDialogFragment(val setPinClickListener:()->Unit) : BaseDialogFragment<FragmentSetPinDialogBinding>() {

    private lateinit var  setPinFragmentVM:SetPinFragmentVM

    override val baseFragmentVM: BaseViewModel
        get() = setPinFragmentVM
    override val customTag: String
        get() = SetPinDialogFragment::class.java.simpleName
    override val layoutId: Int
        get() = R.layout.fragment_set_pin_dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setPinFragmentVM = ViewModelProvider(this).get(SetPinFragmentVM::class.java)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        handelObserver()
    }

    private fun handelObserver() {
        handelEvent()
    }

    private fun handelEvent() {
        setPinFragmentVM.event.observe(viewLifecycleOwner,{
            when(it){
                SetPinFragmentVM.SetPinEvent.SetPinClick -> {
                    setPinClickListener()
                    dismiss()
                }
            }
        })
    }

    private fun setupViews() {
        with(binding){
                lifecycleOwner = viewLifecycleOwner
                viewmodel = setPinFragmentVM
        }
    }
}
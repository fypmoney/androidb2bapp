package com.fypmoney.view.kycagent.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.fypmoney.R
import com.fypmoney.base.BaseBottomSheetFragment
import com.fypmoney.base.BaseViewModel
import com.fypmoney.databinding.BottomSheetDeviceDriverBinding
import com.fypmoney.view.kycagent.viewmodel.DeviceDriverBottomSheetVM

class DeviceDriverBottomSheet : BaseBottomSheetFragment<BottomSheetDeviceDriverBinding>() {

    private val deviceDriverBottomSheetVM by viewModels<DeviceDriverBottomSheetVM> { defaultViewModelProviderFactory }

    override val baseFragmentVM: BaseViewModel
        get() = deviceDriverBottomSheetVM
    override val customTag: String
        get() = DeviceDriverBottomSheet::class.java.simpleName
    override val layoutId: Int
        get() = R.layout.bottom_sheet_device_driver

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpBinding()

        setUpObserver()
    }

    private fun setUpObserver() {
        deviceDriverBottomSheetVM.event.observe(viewLifecycleOwner){
            when(it){
                DeviceDriverBottomSheetVM.DeviceDriverEvent.OnInstallClick -> {
                    dismiss()
                }
            }
        }
    }

    private fun setUpBinding(){
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = deviceDriverBottomSheetVM
        }
    }
}
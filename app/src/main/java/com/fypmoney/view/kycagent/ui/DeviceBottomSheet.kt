package com.fypmoney.view.kycagent.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.fypmoney.R
import com.fypmoney.base.BaseBottomSheetFragment
import com.fypmoney.base.BaseViewModel
import com.fypmoney.databinding.BottomSheetDeviceResultBinding
import com.fypmoney.view.kycagent.viewmodel.DeviceBottomSheetVM

class DeviceBottomSheet : BaseBottomSheetFragment<BottomSheetDeviceResultBinding>() {

    private val deviceBottomSheetVM by viewModels<DeviceBottomSheetVM> { defaultViewModelProviderFactory }

    override val baseFragmentVM: BaseViewModel
        get() = deviceBottomSheetVM
    override val customTag: String
        get() = DeviceBottomSheet::class.java.simpleName
    override val layoutId: Int
        get() = R.layout.bottom_sheet_device_result

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpBinding()

        setUpObserver()
    }

    private fun setUpObserver() {
        deviceBottomSheetVM.event.observe(viewLifecycleOwner){
            when(it){
                DeviceBottomSheetVM.DeviceResultEvent.OnContinueClick -> {
                    dismiss()
                }
            }
        }
    }

    private fun setUpBinding() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = deviceBottomSheetVM
        }
    }
}
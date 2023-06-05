package com.fypmoney.view.kycagent.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.fypmoney.R
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.fypmoney.base.BaseBottomSheetFragment
import com.fypmoney.base.BaseViewModel
import com.fypmoney.databinding.BottomSheetNewKycOptionBinding
import com.fypmoney.view.kycagent.viewmodel.ChooseKycModeBottomSheetVM

class ChooseKycModeBottomSheet : BaseBottomSheetFragment<BottomSheetNewKycOptionBinding>() {

    private val chooseKycModeBottomSheetVM by viewModels<ChooseKycModeBottomSheetVM> { defaultViewModelProviderFactory }

    override val baseFragmentVM: BaseViewModel
        get() = chooseKycModeBottomSheetVM
    override val customTag: String
        get() = ChooseKycModeBottomSheet::class.java.simpleName
    override val layoutId: Int
        get() = R.layout.bottom_sheet_new_kyc_option

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpBinding()

        setUpObserver()

    }

    private fun setUpObserver() {
        chooseKycModeBottomSheetVM.event.observe(viewLifecycleOwner) {
            handleEvents(it)
        }
    }

    private fun handleEvents(it: ChooseKycModeBottomSheetVM.ChooseKycModeEvent?) {
        when (it) {
            ChooseKycModeBottomSheetVM.ChooseKycModeEvent.OnKycOtpClick -> {
                findNavController().navigate(
                    R.id.navigation_enter_mobile_number_kyc,
                    null,
                    navOptions {
                        anim {
                            popEnter = R.anim.slide_in_left
                            popExit = R.anim.slide_out_righ
                            enter = R.anim.slide_in_right
                            exit = R.anim.slide_out_left
                        }
                    })

                dismiss()
            }

            ChooseKycModeBottomSheetVM.ChooseKycModeEvent.OnKycQrCodeClick -> {
                findNavController().navigate(
                    R.id.navigation_qr_code_scan,
                    null,
                    navOptions {
                        anim {
                            popEnter = R.anim.slide_in_left
                            popExit = R.anim.slide_out_righ
                            enter = R.anim.slide_in_right
                            exit = R.anim.slide_out_left
                        }
                    })

                dismiss()
            }

            null -> {}
        }
    }


    private fun setUpBinding() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = chooseKycModeBottomSheetVM
        }
    }
}
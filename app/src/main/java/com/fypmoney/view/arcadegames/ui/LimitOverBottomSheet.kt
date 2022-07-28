package com.fypmoney.view.arcadegames.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.fypmoney.R
import com.fypmoney.base.BaseBottomSheetFragment
import com.fypmoney.base.BaseViewModel
import com.fypmoney.databinding.BottomSheetSpinWheelLimitBinding
import com.fypmoney.view.arcadegames.viewmodel.LimitOverVM

class LimitOverBottomSheet : BaseBottomSheetFragment<BottomSheetSpinWheelLimitBinding>() {

    private val limitOverVM by viewModels<LimitOverVM> { defaultViewModelProviderFactory }
    override val baseFragmentVM: BaseViewModel
        get() = limitOverVM
    override val customTag: String
        get() = LimitOverBottomSheet::class.java.simpleName
    override val layoutId: Int
        get() = R.layout.bottom_sheet_spin_wheel_limit

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpBinding()

        setUpObserver()
    }

    private fun setUpBinding() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            limitViewModel = limitOverVM
        }
    }

    private fun setUpObserver() {
        limitOverVM.event.observe(viewLifecycleOwner) {
            when (it) {
                LimitOverVM.LimitOverEvent.MoreRewardsEvent -> {
                    dismiss()
                    activity?.onBackPressed()
                }
                LimitOverVM.LimitOverEvent.ContinueEvent -> {
                    dismiss()
                    activity?.onBackPressed()
                }
            }
        }
    }
}
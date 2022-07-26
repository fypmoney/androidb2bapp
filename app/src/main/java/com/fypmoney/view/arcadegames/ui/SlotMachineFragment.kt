package com.fypmoney.view.arcadegames.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentSlotMachineBinding
import com.fypmoney.view.arcadegames.viewmodel.SlotMachineFragmentVM
import kotlinx.android.synthetic.main.toolbar.*

class SlotMachineFragment : BaseFragment<FragmentSlotMachineBinding, SlotMachineFragmentVM>() {

    private val slotMachineVM by viewModels<SlotMachineFragmentVM> { defaultViewModelProviderFactory }
    private var mViewBinding: FragmentSlotMachineBinding? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewBinding = getViewDataBinding()

        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = "",
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )

    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_slot_machine
    }

    override fun getViewModel(): SlotMachineFragmentVM = slotMachineVM

    override fun onTryAgainClicked() {
    }

}
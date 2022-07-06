package com.fypmoney.view.arcadegames.ui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentSpinWheelBinding
import com.fypmoney.view.arcadegames.viewmodel.FragmentSpinWheelVM

class SpinWheelFragment : BaseFragment<FragmentSpinWheelBinding, FragmentSpinWheelVM>() {

    private var mViewBinding: FragmentSpinWheelBinding? = null
    private var mViewmodel: FragmentSpinWheelVM? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewBinding = getViewDataBinding()

    }

    override fun onTryAgainClicked() {
        TODO("Not yet implemented")
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_spin_wheel
    }

    override fun getViewModel(): FragmentSpinWheelVM {
        mViewmodel = ViewModelProvider(this).get(FragmentSpinWheelVM::class.java)

        return mViewmodel!!
    }

}
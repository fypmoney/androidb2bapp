package com.fypmoney.view.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.ViewWalkThroughNewTwoBinding
import com.fypmoney.viewmodel.WalkThroughTwoViewModel

/*
* This class is used as walk through screen
* */
class WalkThroughTwoScreen : BaseFragment<ViewWalkThroughNewTwoBinding, WalkThroughTwoViewModel>() {
    private lateinit var mViewModel: WalkThroughTwoViewModel
    private lateinit var mViewBinding: ViewWalkThroughNewTwoBinding


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_walk_through_new_two
    }

    override fun getViewModel(): WalkThroughTwoViewModel {
        mViewModel = ViewModelProvider(this).get(WalkThroughTwoViewModel::class.java)
        return mViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()
        mViewBinding.viewModel = mViewModel
    }

    override fun onTryAgainClicked() {}
}

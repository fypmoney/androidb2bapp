package com.fypmoney.view.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.ViewWalkThroughNewFourBinding
import com.fypmoney.viewmodel.WalkThroughThreeViewModel

/*
* This class is used as Add member Screen
* */
class WalkThroughFourScreen : BaseFragment<ViewWalkThroughNewFourBinding, WalkThroughThreeViewModel>()
{
    private lateinit var mViewModel: WalkThroughThreeViewModel
    private lateinit var mViewBinding: ViewWalkThroughNewFourBinding


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_walk_through_new_four
    }

    override fun getViewModel(): WalkThroughThreeViewModel {
        mViewModel = ViewModelProvider(this).get(WalkThroughThreeViewModel::class.java)
        return mViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()
        mViewBinding.viewModel = mViewModel

    }

    override fun onTryAgainClicked() {

    }


}

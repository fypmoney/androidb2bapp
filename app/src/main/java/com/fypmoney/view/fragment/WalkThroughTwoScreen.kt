package com.fypmoney.view.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.ViewWalkThroughTwoBinding
import com.fypmoney.viewmodel.WalkThroughTwoViewModel
import kotlinx.android.synthetic.main.view_walk_through_one.*
import kotlinx.android.synthetic.main.view_walk_through_two.*
import kotlinx.android.synthetic.main.view_walk_through_two.imageView

/*
* This class is used as walk through screen
* */
class WalkThroughTwoScreen : BaseFragment<ViewWalkThroughTwoBinding, WalkThroughTwoViewModel>() {
    private lateinit var mViewModel: WalkThroughTwoViewModel
    private lateinit var mViewBinding: ViewWalkThroughTwoBinding


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_walk_through_two
    }

    override fun getViewModel(): WalkThroughTwoViewModel {
        mViewModel = ViewModelProvider(this).get(WalkThroughTwoViewModel::class.java)
        return mViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()
        mViewBinding.viewModel = mViewModel
        imageView.gifResource = R.raw.walk_through_two


    }

    override fun onTryAgainClicked() {

    }


}

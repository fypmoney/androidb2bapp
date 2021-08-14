package com.fypmoney.view.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.ViewWalkThroughOneBinding
import com.fypmoney.viewmodel.WalkThroughOneViewModel
import kotlinx.android.synthetic.main.view_walk_through_one.*


/*
* This class is used as Add member Screen
* */
class WalkThroughOneScreen : BaseFragment<ViewWalkThroughOneBinding, WalkThroughOneViewModel>() {
    private lateinit var mViewModel: WalkThroughOneViewModel
    private lateinit var mViewBinding: ViewWalkThroughOneBinding


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_walk_through_one
    }

    override fun getViewModel(): WalkThroughOneViewModel {
        mViewModel = ViewModelProvider(this).get(WalkThroughOneViewModel::class.java)
        return mViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()
        mViewBinding.viewModel = mViewModel
        Glide.with(requireContext()).load(R.raw.walk_through_one).into(imageView)
//        imageView.gifResource = R.raw.walk_through_one

    }

    override fun onTryAgainClicked() {

    }


}

package com.fypmoney.view.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.ViewWalkThroughNewOneBinding
import com.fypmoney.databinding.ViewWalkThroughOneBinding
import com.fypmoney.viewmodel.WalkThroughOneViewModel
import kotlinx.android.synthetic.main.view_walk_through_one.*


/*
* This class is used as Add member Screen
* */
class WalkThroughOneScreen : BaseFragment<ViewWalkThroughNewOneBinding, WalkThroughOneViewModel>() {
    private lateinit var mViewModel: WalkThroughOneViewModel
    private lateinit var mViewBinding: ViewWalkThroughNewOneBinding


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_walk_through_new_one
    }

    override fun getViewModel(): WalkThroughOneViewModel {
        mViewModel = ViewModelProvider(this).get(WalkThroughOneViewModel::class.java)
        return mViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()
        mViewBinding.viewModel = mViewModel }

    override fun onTryAgainClicked() {    }
}

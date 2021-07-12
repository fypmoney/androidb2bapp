package com.fypmoney.view.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.ViewWalkThroughThreeBinding
import com.fypmoney.viewmodel.WalkThroughThreeViewModel
import kotlinx.android.synthetic.main.view_walk_through_one.*

/*
* This class is used as Add member Screen
* */
class WalkThroughThreeScreen : BaseFragment<ViewWalkThroughThreeBinding, WalkThroughThreeViewModel>()
{
    private lateinit var mViewModel: WalkThroughThreeViewModel
    private lateinit var mViewBinding: ViewWalkThroughThreeBinding


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_walk_through_three
    }

    override fun getViewModel(): WalkThroughThreeViewModel {
        mViewModel = ViewModelProvider(this).get(WalkThroughThreeViewModel::class.java)
        return mViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()
        mViewBinding.viewModel = mViewModel
        Glide.with(requireContext()).load(R.raw.walk_through_three).into(imageView)
    }

    override fun onTryAgainClicked() {

    }


}

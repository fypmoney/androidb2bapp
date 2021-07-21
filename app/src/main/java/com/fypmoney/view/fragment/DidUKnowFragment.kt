package com.fypmoney.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentDidUKnowBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.viewmodel.DidUKnowViewModel


/**
 * This fragment is used for Adding money
 */
class DidUKnowFragment : BaseFragment<FragmentDidUKnowBinding, DidUKnowViewModel>() {

    private lateinit var mViewModel: DidUKnowViewModel
    private lateinit var mViewBinding: FragmentDidUKnowBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_did_u_know
    }

    override fun getViewModel(): DidUKnowViewModel {
        mViewModel = ViewModelProvider(this).get(DidUKnowViewModel::class.java)
        return mViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()
        mViewBinding.viewModel = mViewModel
        mViewModel.resourceId.set(arguments?.getString(AppConstants.FEED_TYPE_DID_YOU_KNOW)!!)
        setObservers()

    }

    /*
    * This method is used to observe the observers
    * */
    private fun setObservers() {

    }

    override fun onTryAgainClicked() {

    }


}

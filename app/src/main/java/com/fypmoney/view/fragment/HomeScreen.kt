package com.fypmoney.view.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.viewmodel.HomeScreenViewModel
import com.fypmoney.BR
import com.fypmoney.databinding.ScreenHomeBinding


/**
 * This fragment is used for loyalty
 */
class HomeScreen : BaseFragment<ScreenHomeBinding, HomeScreenViewModel>() {

    private lateinit var mViewModel: HomeScreenViewModel
    private lateinit var mViewBinding: ScreenHomeBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.screen_home
    }

    override fun getViewModel(): HomeScreenViewModel {
        mViewModel = ViewModelProvider(this).get(HomeScreenViewModel::class.java)
        return mViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()
        mViewBinding.viewModel = mViewModel
        setObservers()

    }

    /*
    * This method is used to observe the observers
    * */
    private fun setObservers() {


    }


}

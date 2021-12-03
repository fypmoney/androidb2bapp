package com.fypmoney.view.home.main.fyper.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentFyperBinding
import com.fypmoney.view.home.main.fyper.viewmodel.FyperFragmentVM

class FyperFragment : BaseFragment<FragmentFyperBinding,FyperFragmentVM>() {

    private val fyperFragmentVM by viewModels<FyperFragmentVM> {
        defaultViewModelProviderFactory
    }
    private lateinit var binding: FragmentFyperBinding

    companion object {
        fun newInstance() = FyperFragment()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
    }



    override fun onTryAgainClicked() {
    }

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    override fun getBindingVariable(): Int = BR.viewModel

    /**
     * @return layout resource id
     */
    override fun getLayoutId(): Int  = R.layout.fragment_fyper

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): FyperFragmentVM  = fyperFragmentVM

}
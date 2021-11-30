package com.fypmoney.view.home.main.home.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentHomeBinding
import com.fypmoney.view.home.main.home.viewmodel.HomeFragmentVM

class HomeFragment : BaseFragment<FragmentHomeBinding,HomeFragmentVM>() {

    private  val homeFragmentVM by viewModels<HomeFragmentVM> {
        defaultViewModelProviderFactory
    }
    private lateinit var  _binding: FragmentHomeBinding


    private val binding get() = _binding

    override fun onTryAgainClicked() {

    }

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    override fun getBindingVariable(): Int  = BR.viewModel

    /**
     * @return layout resource id
     */
    override fun getLayoutId(): Int  = R.layout.fragment_home

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): HomeFragmentVM  = homeFragmentVM


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = getViewDataBinding()
    }


}
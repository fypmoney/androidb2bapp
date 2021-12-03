package com.fypmoney.view.home.main.explore.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentExploreBinding
import com.fypmoney.view.home.main.explore.viewmodel.ExploreFragmentVM

class ExploreFragment : BaseFragment<FragmentExploreBinding,ExploreFragmentVM>() {

    private  val exploreFragmentVM by viewModels<ExploreFragmentVM> { defaultViewModelProviderFactory }
    private lateinit var _binding: FragmentExploreBinding

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding


    override fun onTryAgainClicked() {
        TODO("Not yet implemented")
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
    override fun getLayoutId(): Int  = R.layout.fragment_explore

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): ExploreFragmentVM  = exploreFragmentVM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = getViewDataBinding()
    }
}
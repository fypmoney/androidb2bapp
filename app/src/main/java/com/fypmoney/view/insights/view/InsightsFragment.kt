package com.fypmoney.view.insights.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentInsightsBinding
import com.fypmoney.view.insights.viewmodel.InsightsFragmentVM

class InsightsFragment : BaseFragment<FragmentInsightsBinding,InsightsFragmentVM>() {

    companion object {
        fun newInstance() = InsightsFragment()
    }


    private  val insightsFragmentVM by viewModels<InsightsFragmentVM> { defaultViewModelProviderFactory }
    private lateinit var binding: FragmentInsightsBinding


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
    override fun getLayoutId(): Int =R.layout.fragment_insights

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): InsightsFragmentVM  = insightsFragmentVM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
    }

}
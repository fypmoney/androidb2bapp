package com.fypmoney.view.insights.view

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentInsightsBinding
import com.fypmoney.view.insights.adapter.SpendAndIncomeStateAdapter
import com.fypmoney.view.insights.viewmodel.InsightsFragmentVM
import com.google.android.material.tabs.TabLayoutMediator

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
        setUpViews()
    }

    private fun setUpViews() {
        binding.pager.adapter  = SpendAndIncomeStateAdapter(this)
        TabLayoutMediator(binding.tabLayoutSpendsIncome, binding.pager) { tab, position ->
            when(position){
                0->{
                    tab.icon = ContextCompat.getDrawable(requireContext(),R.drawable.ic_up_arrow)
                    tab.text = getString(R.string.spends)
                }
                1->{
                    tab.icon = ContextCompat.getDrawable(requireContext(),R.drawable.ic_down_arrow)
                    tab.text = getString(R.string.income)
                }
            }
        }.attach()
    }

}
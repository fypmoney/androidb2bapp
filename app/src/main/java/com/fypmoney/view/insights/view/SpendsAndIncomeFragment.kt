package com.fypmoney.view.insights.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentSpendsAndIncomeBinding
import com.fypmoney.view.insights.viewmodel.InsightsFragmentVM

class SpendsAndIncomeFragment : BaseFragment<FragmentSpendsAndIncomeBinding, InsightsFragmentVM>() {

    private val spendsAndIncomeFragmentVM by viewModels<InsightsFragmentVM> ({ requireParentFragment() })

    private lateinit var binding:FragmentSpendsAndIncomeBinding

    companion object {
        fun newInstance() = SpendsAndIncomeFragment()
    }


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
    override fun getLayoutId(): Int  = R.layout.fragment_spends_and_income

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): InsightsFragmentVM  = spendsAndIncomeFragmentVM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
        setupViews()
    }

    private fun setupViews() {

    }

}
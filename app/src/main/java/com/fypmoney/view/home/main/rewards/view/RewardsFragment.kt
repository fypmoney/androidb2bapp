package com.fypmoney.view.home.main.rewards.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentRewardsBinding
import com.fypmoney.view.home.main.rewards.viewmodel.RewardsFragmentVM
import com.fypmoney.BR
import com.fypmoney.R

class RewardsFragment : BaseFragment<FragmentRewardsBinding,RewardsFragmentVM>() {

    private  val rewardsFragmentVM by viewModels<RewardsFragmentVM> {
        defaultViewModelProviderFactory
    }
    private lateinit var _binding: FragmentRewardsBinding

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
    override fun getLayoutId(): Int  = R.layout.fragment_rewards

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): RewardsFragmentVM  = rewardsFragmentVM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = getViewDataBinding()
    }
}
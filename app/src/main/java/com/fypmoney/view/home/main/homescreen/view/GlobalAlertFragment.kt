package com.fypmoney.view.home.main.homescreen.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentGlobalAlertBinding
import com.fypmoney.util.SharedPrefUtils

class GlobalAlertFragment : BaseFragment<FragmentGlobalAlertBinding,GlobalAlertFragmentVM>() {

    companion object {
        fun newInstance() = GlobalAlertFragment()
    }

    private  val globalAlertFragmentVM by viewModels<GlobalAlertFragmentVM> { defaultViewModelProviderFactory }
    private lateinit var binding:FragmentGlobalAlertBinding



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
    override fun getLayoutId(): Int  = R.layout.fragment_global_alert

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): GlobalAlertFragmentVM  = globalAlertFragmentVM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
        SharedPrefUtils.getString(
            PockketApplication.instance,
            SharedPrefUtils.SF_SERVER_MAINTENANCE_DESCRIPTION)?.let { msg->
            binding.tvSubHeading.text = msg
        }
    }

}
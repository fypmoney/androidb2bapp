package com.fypmoney.view.home.main.home.view

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentHomeBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.activity.AddMoneyView
import com.fypmoney.view.activity.ContactListView
import com.fypmoney.view.home.main.home.adapter.CallToActionAdapter
import com.fypmoney.view.home.main.home.adapter.QuickActionAdapter
import com.fypmoney.view.home.main.home.viewmodel.HomeFragmentVM
import com.fypmoney.view.home.main.homescreen.view.LoadMoneyBottomSheet
import com.fypmoney.view.storeoffers.OffersScreen

class HomeFragment : BaseFragment<FragmentHomeBinding, HomeFragmentVM>() {

    private val homeFragmentVM by viewModels<HomeFragmentVM> {
        defaultViewModelProviderFactory
    }
    private lateinit var _binding: FragmentHomeBinding


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

    override fun onStart() {
        super.onStart()
        homeFragmentVM.fetchBalance()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = getViewDataBinding()
        setupRecyclerView()
        setUpObserver()
        homeFragmentVM.prepareQuickActionList()

    }



    private fun setupRecyclerView() {
        with(binding.quickActionRv) {
            adapter = QuickActionAdapter(viewLifecycleOwner, onQuickActionClicked = {
                when(it.id){
                    HomeFragmentVM.QuickActionEvent.AddAction -> {
                        val intent = Intent(requireActivity(), AddMoneyView::class.java)
                        startActivity(intent)
                    }
                    HomeFragmentVM.QuickActionEvent.OfferAction -> {
                        val intent = Intent(requireActivity(), OffersScreen::class.java)
                        startActivity(intent)
                    }
                    HomeFragmentVM.QuickActionEvent.PayAction -> {
                        val intent = Intent(requireActivity(), ContactListView::class.java)
                        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, AppConstants.PAY)
                        startActivity(intent)
                    }
                }
            })
        }
        with(binding.callToActionRv) {
            adapter = CallToActionAdapter(viewLifecycleOwner, onCallToActionClicked = {

            })
        }
    }

    private fun setUpObserver() {
        homeFragmentVM.event.observe(viewLifecycleOwner,{
            handelEvents(it)
        })
        homeFragmentVM.state.observe(viewLifecycleOwner,{
            handelState(it)
        })
    }

    private fun handelState(it: HomeFragmentVM.HomeFragmentState?) {
        when(it){
            HomeFragmentVM.HomeFragmentState.ErrorBalanceState -> {
                //binding.loadingBalanceHdp.toGone()

            }
            HomeFragmentVM.HomeFragmentState.LoadingBalanceState -> {
                binding.loadingBalanceHdp.clearAnimation()
                binding.walletBalanceTv.toGone()
                binding.lowBalanceTv.toGone()
                binding.loadingBalanceHdp.toVisible()
            }
            is HomeFragmentVM.HomeFragmentState.SuccessBalanceState -> {
                binding.loadingBalanceHdp.clearAnimation()
                binding.loadingBalanceHdp.toGone()
                binding.walletBalanceTv.toVisible()
                binding.walletBalanceTv.text = """${getString(R.string.Rs)}${Utility.convertToRs(it.balance.toString())}"""
            }
            is HomeFragmentVM.HomeFragmentState.LowBalanceAlertState -> {
                if(it.balanceIsLow){
                    binding.lowBalanceTv.toVisible()
                }else{
                    binding.lowBalanceTv.toGone()
                }
            }
            is HomeFragmentVM.HomeFragmentState.SuccessCallToActionState -> {
                binding.shimmerLayout.toGone()
                binding.callToActionRv.toVisible()
                (binding.callToActionRv.adapter as CallToActionAdapter).submitList(it.callToActionList)
            }
            HomeFragmentVM.HomeFragmentState.LoadingCallToActionState -> TODO()
            HomeFragmentVM.HomeFragmentState.ShowLoadMoneySheetState -> {
                val loadMoneyBottomSheet = LoadMoneyBottomSheet()
                loadMoneyBottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
                loadMoneyBottomSheet.show(childFragmentManager, "LoadMoneySheet")
            }
            null -> TODO()
        }
    }

    private fun handelEvents(it: HomeFragmentVM.HomeFragmentEvent?) {
        when(it){
            is HomeFragmentVM.HomeFragmentEvent.QuickActionListReady -> {
                (binding.quickActionRv.adapter as QuickActionAdapter).submitList(it.quickActionList)
                //homeFragmentVM.prepareCallToActionList()
                homeFragmentVM.callToAction()
            }
            HomeFragmentVM.HomeFragmentEvent.ViewCardDetails -> {
                findNavController().navigate(R.id.navigation_card)

            }
            null -> TODO()
        }
    }

}
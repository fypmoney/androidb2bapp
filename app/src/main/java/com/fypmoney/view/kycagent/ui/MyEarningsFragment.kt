package com.fypmoney.view.kycagent.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentMyEarningsBinding
import com.fypmoney.extension.toInvisible
import com.fypmoney.extension.toVisible
import com.fypmoney.util.Utility
import com.fypmoney.view.kycagent.adapter.MyEarningsAdapter
import com.fypmoney.view.kycagent.adapter.MyEarningsUiModel
import com.fypmoney.view.kycagent.viewmodel.MyEarningsFragmentVM
import kotlinx.android.synthetic.main.toolbar.*

class MyEarningsFragment : BaseFragment<FragmentMyEarningsBinding, MyEarningsFragmentVM>() {

    private lateinit var binding: FragmentMyEarningsBinding
    private val myEarningsFragmentVM by viewModels<MyEarningsFragmentVM> { defaultViewModelProviderFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = getViewDataBinding()

        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = "My Earnings",
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )

        trackr {
            it.name = TrackrEvent.earnings_view
        }
        setUpRecyclerView()

        setUpObserver()
    }

    private fun setUpObserver() {
        myEarningsFragmentVM.state.observe(viewLifecycleOwner){
            handleState(it)
        }
    }

    private fun handleState(it: MyEarningsFragmentVM.MyEarningsState?) {

        when(it){
            is MyEarningsFragmentVM.MyEarningsState.Error -> {
                Utility.showToast("Error: ${it.errorResponseInfo.msg}")
            }
            MyEarningsFragmentVM.MyEarningsState.Loading -> {

            }
            is MyEarningsFragmentVM.MyEarningsState.Success -> {
                setDataOnUi(it)
            }
            null -> {

            }
        }
    }

    private fun setDataOnUi(it: MyEarningsFragmentVM.MyEarningsState.Success) {
        binding.tvTotalEarnings.text = "₹ " + myEarningsFragmentVM.totalEarnings

        binding.shimmerLayoutMyEarnings.toInvisible()

        if (it.listOfEarnings.isEmpty()) {
            binding.emptyMyEarnings.toVisible()
            binding.recyclerMyEarnings.toInvisible()
        } else {
            binding.emptyMyEarnings.toInvisible()
            binding.recyclerMyEarnings.toVisible()
        }

        (binding.recyclerMyEarnings.adapter as MyEarningsAdapter).submitList(
            it.listOfEarnings.map { earningItem ->
                earningItem?.let { it1 -> MyEarningsUiModel.fromEarningsItem(it1) }
            }
        )
    }

    private fun setUpRecyclerView(){
        val earningsAdapter = MyEarningsAdapter()
        binding.recyclerMyEarnings.adapter = earningsAdapter
        binding.recyclerMyEarnings.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
    }

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.fragment_my_earnings

    override fun getViewModel(): MyEarningsFragmentVM = myEarningsFragmentVM

    override fun onTryAgainClicked() {}

}
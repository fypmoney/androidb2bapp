package com.fypmoney.view.recharge


import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.SelectCircleFragmentBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.util.AppConstants
import com.fypmoney.view.recharge.adapter.CircleSelectionAdapter
import com.fypmoney.view.recharge.viewmodel.SelectCircleFragmentVM
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ObsoleteCoroutinesApi


/*
* This class is used as Home Screen
* */
@FlowPreview
@ObsoleteCoroutinesApi
class SelectCircleFragment : BaseFragment<SelectCircleFragmentBinding, SelectCircleFragmentVM>() {
    private lateinit var selectCircleFragmentVM: SelectCircleFragmentVM
    private lateinit var mViewBinding: SelectCircleFragmentBinding
    private val args: SelectCircleFragmentArgs by navArgs()

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.select_circle_fragment
    }

    override fun getViewModel(): SelectCircleFragmentVM {
        selectCircleFragmentVM = ViewModelProvider(this).get(SelectCircleFragmentVM::class.java)
        return selectCircleFragmentVM
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectCircleFragmentVM.selectedOperator.value = args.selectedOperator
        selectCircleFragmentVM.mobile.value = args.mobile
        mViewBinding = getViewDataBinding()
        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar, backArrowTint = Color.WHITE,
            titleColor = Color.WHITE,
            isBackArrowVisible = true,
            toolbarTitle = getString(R.string.select_your_circle)
        )
        args.rechargeType.let {
            selectCircleFragmentVM.rechargeType.value = it

        }
        setUpRecyclerView()
        setObserver()
        selectCircleFragmentVM.callGetCircleList()


    }

    override fun onTryAgainClicked() {

    }

    private fun setUpRecyclerView() {
        val circleAdapter = CircleSelectionAdapter(
            this, onCircleClick = {
                selectCircleFragmentVM.rechargeType.value.let { itstr ->
                    if (itstr == AppConstants.POSTPAID) {
                        val directions =
                            it.name?.let { it1 ->
                                selectCircleFragmentVM.mobile.value?.let { it2 ->
                                    SelectCircleFragmentDirections.actionPostpaidBill(
                                        selectCircleFragmentVM.selectedOperator.value,
                                        selectedCircle = it1,
                                        mobile = it2
                                    )
                                }
                            }


                        directions?.let { it1 -> findNavController().navigate(it1) }
                    } else {
                        val directions =
                            it.name?.let { it1 ->
                                selectCircleFragmentVM.mobile.value?.let { it2 ->
                                    SelectCircleFragmentDirections.actionSelectRechargePlans(
                                        selectCircleFragmentVM.selectedOperator.value,
                                        selectedCircle = it1,
                                        mobile = it2,
                                        rechargeTye = selectCircleFragmentVM.rechargeType.value
                                    )
                                }
                            }
                        directions?.let { it1 -> findNavController().navigate(it1) }
                    }
                }


            }
        )


        with(mViewBinding.rvCircles) {
            adapter = circleAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }
    }

    private fun setObserver() {
        selectCircleFragmentVM.state.observe(viewLifecycleOwner){
            handelState(it)
        }
    }

    private fun handelState(it: SelectCircleFragmentVM.SelectCircleState?) {
        when(it){
            is SelectCircleFragmentVM.SelectCircleState.Error -> {
                mViewBinding.noCircleFoundTv.toVisible()
                mViewBinding.shimmerLayout.toGone()
                mViewBinding.rvCircles.toGone()
            }
            SelectCircleFragmentVM.SelectCircleState.Loading -> {
                mViewBinding.noCircleFoundTv.toGone()
                mViewBinding.shimmerLayout.toVisible()
                mViewBinding.rvCircles.toGone()
            }
            is SelectCircleFragmentVM.SelectCircleState.Success -> {
                mViewBinding.noCircleFoundTv.toGone()
                mViewBinding.shimmerLayout.toGone()
                mViewBinding.rvCircles.toVisible()
                (mViewBinding.rvCircles.adapter as CircleSelectionAdapter).submitList(it.circles)
            }
            null -> TODO()
            SelectCircleFragmentVM.SelectCircleState.Empty -> {
                mViewBinding.noCircleFoundTv.toVisible()
                mViewBinding.noCircleFoundTv.text = getString(R.string.no_circle_found_according_to_your_search)
                mViewBinding.shimmerLayout.toGone()
                mViewBinding.rvCircles.toGone()
            }
        }
    }


}

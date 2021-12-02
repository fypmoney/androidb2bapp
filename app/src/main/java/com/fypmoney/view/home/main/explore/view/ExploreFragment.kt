package com.fypmoney.view.home.main.explore.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentExploreBinding
import com.fypmoney.model.FeedDetails
import com.fypmoney.view.home.main.explore.adapters.ExploreAdapter
import com.fypmoney.view.home.main.explore.adapters.gamersAdapter
import com.fypmoney.view.home.main.explore.model.ExploreContentResponse
import com.fypmoney.view.home.main.explore.model.SectionContentItem
import com.fypmoney.view.home.main.explore.viewmodel.ExploreFragmentVM

class ExploreFragment : BaseFragment<FragmentExploreBinding, ExploreFragmentVM>(),
    ExploreAdapter.OnFeedItemClickListener {

    private val exploreFragmentVM by viewModels<ExploreFragmentVM> { defaultViewModelProviderFactory }
    private lateinit var _binding: FragmentExploreBinding

    // This property is only valid between onCreateView and
    // onDestroyView.
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
    override fun getLayoutId(): Int  = R.layout.fragment_explore
    private var itemsArrayList: ArrayList<ExploreContentResponse> = ArrayList()

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): ExploreFragmentVM = exploreFragmentVM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = getViewDataBinding()

        setRecyclerViewGamers(_binding)
        setObserver()

    }

    private fun setObserver() {
        exploreFragmentVM?.rewardHistoryList.observe(
            requireActivity(),
            { list ->
                setRecyclerView(_binding, list)
            })
    }

    private fun setRecyclerView(
        root: FragmentExploreBinding,
        list: ArrayList<ExploreContentResponse>
    ) {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        root.rvExplore.layoutManager = layoutManager


        var typeAdapter = ExploreAdapter(exploreFragmentVM, this, list[1].sectionContent)
        root.rvExplore.adapter = typeAdapter
    }

    private fun setRecyclerViewGamers(
        root: FragmentExploreBinding
    ) {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        root.rvCards.layoutManager = layoutManager


        var typeAdapter = gamersAdapter(requireContext())
        root.rvCards.adapter = typeAdapter
    }


    override fun onFeedClick(position: Int, feedDetails: SectionContentItem) {

    }

}
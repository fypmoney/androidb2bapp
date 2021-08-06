package com.fypmoney.view.fragment

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.ScreenHomeBinding
import com.fypmoney.listener.LocationListenerClass
import com.fypmoney.model.CustomerInfoResponseDetails
import com.fypmoney.model.FeedDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.view.activity.*
import com.fypmoney.view.adapter.TopTenUsersAdapter
import com.fypmoney.view.referandearn.view.ReferAndEarnActivity
import com.fypmoney.viewmodel.HomeScreenViewModel
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.screen_home.*



class HomeScreen : BaseFragment<ScreenHomeBinding, HomeScreenViewModel>(),
    LocationListenerClass.GetCurrentLocationListener {

    private lateinit var mViewModel: HomeScreenViewModel
    private lateinit var mViewBinding: ScreenHomeBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.screen_home
    }

    override fun getViewModel(): HomeScreenViewModel {
        mViewModel = ViewModelProvider(this).get(HomeScreenViewModel::class.java)
        return mViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()
        mViewBinding.viewModel = mViewModel
        choreCard.setOnClickListener {
            intentToPayActivity(ChoresActivity::class.java)
        }
        LocationListenerClass(
            requireActivity(), this
        ).permissions()
        setObservers()
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        val topTenUsersAdapter = TopTenUsersAdapter(
            viewLifecycleOwner
        )
        with(mViewBinding.recentRv){
            adapter = topTenUsersAdapter
            layoutManager =  LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        }
    }

    /*
    * This method is used to observe the observers
    * */
    private fun setObservers() {
        mViewModel.topTenUsersResponse.observe(viewLifecycleOwner,{
            if(it.data.isNullOrEmpty()){
                mViewBinding.recentTv.visibility = View.GONE
                mViewBinding.recentRv.visibility = View.GONE
            }else{
                mViewBinding.recentTv.visibility = View.VISIBLE
                mViewBinding.recentRv.visibility = View.VISIBLE
                (mViewBinding.recentRv.adapter as TopTenUsersAdapter).run {
                    submitList(it.data)
                }
            }
        })
        mViewModel.onAddMoneyClicked.observe(viewLifecycleOwner) {
            if (it) {
                callActivity(AddMoneyView::class.java)
                mViewModel.onAddMoneyClicked.value = false
            }
        }
        mViewModel.onReferalAndCodeClicked.observe(viewLifecycleOwner) {
            if (it) {
                callActivity(ReferAndEarnActivity::class.java)
                mViewModel.onReferalAndCodeClicked.value = false
            }
        }
        mViewModel.onPayClicked.observe(viewLifecycleOwner) {
            if (it) {
                intentToPayActivity(ContactListView::class.java, AppConstants.PAY)
                mViewModel.onPayClicked.value = false
            }
        }

        mViewModel.onFeedButtonClick.observe(viewLifecycleOwner) {
                    when (it.displayCard) {
                        AppConstants.FEED_TYPE_DEEPLINK -> {
                            try {
                                intentToActivity(
                                    Class.forName(AppConstants.BASE_ACTIVITY_URL + it.action?.url!!),
                                    it
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                                intentToActivity(HomeView::class.java, it)
                            }
                        }
                        AppConstants.FEED_TYPE_INAPPWEB -> {
                            intentToActivity(
                                UserFeedsDetailView::class.java,
                                it,
                                AppConstants.FEED_TYPE_INAPPWEB
                            )
                        }
                        AppConstants.FEED_TYPE_BLOG -> {
                            intentToActivity(
                                UserFeedsDetailView::class.java,
                                it,
                                AppConstants.FEED_TYPE_BLOG
                            )
                        }
                        AppConstants.FEED_TYPE_EXTWEBVIEW -> {
                            startActivity(
                                Intent.createChooser(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse(it.action?.url)
                                    ), getString(R.string.browse_with)
                                )
                            )

                        }
                    }
        }


        mViewModel.fetchBalanceLoading.observe(viewLifecycleOwner) {
            if (it) {
                mViewBinding.amountFetching.clearAnimation()
                mViewBinding.amountFetching.visibility = View.GONE
                mViewModel.fetchBalanceLoading.value = false
            }
        }

    }

    override fun onTryAgainClicked() {

    }


    private fun setCurrentFragment(fragment: Fragment) =
        childFragmentManager.beginTransaction().apply {
            replace(R.id.container, fragment)
            commit()
        }

    /*
    * This method is used to call add money fragment
    * */
    private fun callActivity(aClass: Class<*>) {
        val intent = Intent(requireActivity(), aClass)
        requireContext().startActivity(intent)
    }

    private fun intentToPayActivity(aClass: Class<*>, pay: String? = null) {
        val intent = Intent(requireActivity(), aClass)
        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, pay)
        requireContext().startActivity(intent)
    }

    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(aClass: Class<*>, feedDetails: FeedDetails, type: String? = null) {
        val intent = Intent(context, aClass)
        intent.putExtra(AppConstants.FEED_RESPONSE, feedDetails)
        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, type)
        intent.putExtra(AppConstants.CUSTOMER_INFO_RESPONSE, CustomerInfoResponseDetails())
        startActivity(intent)
    }

    override fun getCurrentLocation(
        isInternetConnected: Boolean?,
        latitude: Double,
        Longitude: Double
    ) {
        mViewModel.latitude.set(latitude)
        mViewModel.longitude.set(Longitude)
        mViewModel.callFetchFeedsApi(false, latitude, Longitude)
    }

    /*
   * This method is used to call card settings
   * */
    private fun callDiduKnowBottomSheet(list: ArrayList<String?>) {
        val bottomSheet =
            DidUKnowBottomSheet(list)
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(childFragmentManager, "DidUKnowSheet")
    }

}

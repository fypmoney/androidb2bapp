package com.fypmoney.view.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.ViewUserFeedsBinding
import com.fypmoney.model.CustomerInfoResponseDetails
import com.fypmoney.model.FeedDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.util.AppConstants.FEED_RESPONSE
import com.fypmoney.util.DialogUtils
import com.fypmoney.util.Utility
import com.fypmoney.util.Utility.deeplinkRedirection
import com.fypmoney.view.fypstories.view.StoriesBottomSheet
import com.fypmoney.view.interfaces.HomeTabChangeClickListener
import com.fypmoney.viewmodel.FeedsViewModel
import kotlinx.android.synthetic.main.view_user_feeds.*

/*
* This is used to show list of feeds
* */
class UserFeedsView(val tabchangeListner: HomeTabChangeClickListener) :
    BaseFragment<ViewUserFeedsBinding, FeedsViewModel>(),
    DialogUtils.OnAlertDialogClickListener {
    private lateinit var mViewModel: FeedsViewModel
    private lateinit var mViewBinding: ViewUserFeedsBinding
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }


    override fun getLayoutId(): Int {
        return R.layout.view_user_feeds
    }

    override fun getViewModel(): FeedsViewModel {
        mViewModel = ViewModelProvider(this).get(FeedsViewModel::class.java)
        return mViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()
        /*setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar,
            isBackArrowVisible = false
        )*/

        trackr {
            it.name = TrackrEvent.feed_launch
        }

        mViewModel.callFetchFeedsApi(false, 0.0, 0.0)


        setObserver()
    }



    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
       /* mViewModel.onFeedsSuccess.observe(viewLifecycleOwner)
        {
            mViewModel.fromWhichScreen.set(0)
            callDiduKnowBottomSheet(it)

        }*/
        mViewModel.onFeedButtonClick.observe(viewLifecycleOwner) {
            when (it.displayCard) {
                        AppConstants.FEED_TYPE_DEEPLINK -> {
                            it.action?.url?.let {

                                val screen = it.split(",")[0]
                                if (screen == AppConstants.StoreScreen || screen == AppConstants.CardScreen || screen == AppConstants.FEEDSCREEN || screen == AppConstants.FyperScreen) {
                                    tabchangeListner.tabchange(0, screen)
                                } else {
                                    Utility.deeplinkRedirection(screen, requireContext())

                                }


                            }

                        }
                AppConstants.FEED_TYPE_INAPPWEB -> {
                    intentToActivity(
                        UserFeedsInAppWebview::class.java,
                        it,
                        AppConstants.FEED_TYPE_INAPPWEB
                    )
                }
                AppConstants.FEED_TYPE_INAPPWEB2 -> {
                    intentToActivity(
                        UserFeedsInAppWebview::class.java,
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
                AppConstants.FEED_TYPE_EXTWEBVIEW2 -> {
                    startActivity(
                        Intent.createChooser(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(it.action?.url)
                            ), getString(R.string.browse_with)
                        )
                    )

                }
                AppConstants.FEED_TYPE_STORIES -> {
                    if (!it.resourceArr.isNullOrEmpty()) {
                        callDiduKnowBottomSheet(it.resourceArr)
                    }

                }
            }




        }

        mViewModel.onFeedsApiFail.observe(viewLifecycleOwner) {
            if (it) {
                mViewModel.noDataFoundVisibility.set(true)
                mViewModel.noDataText.set("")
                mViewModel.onFeedsApiFail.value = false
            }

        }


    }

    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(aClass: Class<*>, feedDetails: FeedDetails, type: String? = null) {
        val intent = Intent(context, aClass)
        intent.putExtra(FEED_RESPONSE, feedDetails)
        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, type)
        intent.putExtra(AppConstants.CUSTOMER_INFO_RESPONSE, CustomerInfoResponseDetails())
        startActivity(intent)
    }





    override fun onResume() {
        super.onResume()
        shimmerLayout.startShimmer()
    }



    override fun onTryAgainClicked() {
        shimmerLayout.startShimmer()
        mViewModel.isRecyclerviewVisible.set(false)
        mViewModel.callFetchFeedsApi(
            latitude = mViewModel.latitude.get(),
            longitude = mViewModel.longitude.get()
        )
    }

    /*
 * This method is used to call card settings
 * */
    private fun callDiduKnowBottomSheet(list: List<String>) {
        val bottomSheet =
            StoriesBottomSheet(list)
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(childFragmentManager, "DidUKnowSheet")
    }

    override fun onPositiveButtonClick(uniqueIdentifier: String) {
        mViewModel.isRecyclerviewVisible.set(true)
        shimmerLayout.startShimmer()
        mViewModel.callFetchFeedsApi(false, mViewModel.latitude.get(), mViewModel.longitude.get())
    }
}

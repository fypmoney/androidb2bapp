package com.fypmoney.view.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.ViewUserFeedsBinding
import com.fypmoney.listener.LocationListenerClass
import com.fypmoney.listener.requestCodeGPSAddress
import com.fypmoney.model.CustomerInfoResponseDetails
import com.fypmoney.model.FeedDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.util.AppConstants.BASE_ACTIVITY_URL
import com.fypmoney.util.AppConstants.FEED_RESPONSE
import com.fypmoney.util.DialogUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.fragment.CardSettingsBottomSheet
import com.fypmoney.view.fragment.DidUKnowBottomSheet
import com.fypmoney.viewmodel.FeedsViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_user_feeds.*

/*
* This is used to show list of feeds
* */
class UserFeedsView : BaseFragment<ViewUserFeedsBinding, FeedsViewModel>(),
    LocationListenerClass.GetCurrentLocationListener, DialogUtils.OnAlertDialogClickListener {
    private lateinit var mViewModel: FeedsViewModel
    private lateinit var mViewBinding: ViewUserFeedsBinding
    var isLocationPermissionAllowed = ObservableField(false)
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
        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar,
            isBackArrowVisible = false
        )

        checkAndAskPermission()


        setObserver()k
    }

    private fun checkAndAskPermission() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), requestCodeGPSAddress
            )
        } else {
            LocationListenerClass(
                requireActivity(), this
            ).permissions()
        }

    }


    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        mViewModel.onFeedsSuccess.observe(viewLifecycleOwner)
        {
            mViewModel.fromWhichScreen.set(0)
            callDiduKnowBottomSheet(it)

        }
        mViewModel.onFeedButtonClick.observe(viewLifecycleOwner) {
            when (mViewModel.selectedPosition.get()) {
                0 -> {
                    mViewModel.fromWhichScreen.set(1)
                    mViewModel.isApiLoading.set(true)
                    mViewModel.callFetchFeedsApi(
                        isProgressBarVisible = true,
                        latitude = mViewModel.latitude.get(),
                        longitude = mViewModel.longitude.get()
                    )

                }
                else -> {
                    when (it.displayCard) {
                        AppConstants.FEED_TYPE_DEEPLINK -> {
                            try {
                                intentToActivity(
                                    Class.forName(BASE_ACTIVITY_URL + it.action?.url!!),
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
            }


        }

        mViewModel.onFeedsApiFail.observe(viewLifecycleOwner) {
            if (it) {
                shimmerLayout.stopShimmerAnimation()
                mViewModel.noDataFoundVisibility.set(true)
                mViewModel.noDataText.set("")
                mViewModel.onFeedsApiFail.value = false
                DialogUtils.showConfirmationDialog(
                    context = requireContext(),
                    title = "",
                    message = getString(R.string.empty_Feeds_error),
                    positiveButtonText = getString(R.string.try_again_text),
                    negativeButtonText = getString(R.string.cancel_btn_text),
                    uniqueIdentifier = "",
                    onAlertDialogClickListener = this, isNegativeButtonRequired = true
                )
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

    override fun getCurrentLocation(
        isInternetConnected: Boolean?,
        latitude: Double,
        Longitude: Double
    ) {
        mViewModel.latitude.set(latitude)
        mViewModel.longitude.set(Longitude)
        mViewModel.callFetchFeedsApi(false, latitude, Longitude)


    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (permission in permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    permission
                )
            ) {
                //denied
                    if (mViewModel.isDenied.get() == false) {
                    mViewModel.callFetchFeedsApi(false, 0.0, 0.0)
                    mViewModel.isDenied.set(true)
                }
            } else {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        permission
                    ) == PackageManager.PERMISSION_GRANTED
                ) {

                    //allow
                    if (!isLocationPermissionAllowed.get()!!) {
                        LocationListenerClass(
                            requireActivity(), this
                        ).permissions()
                    }
                    isLocationPermissionAllowed.set(true)
                    /*  mViewModel.callFetchFeedsApi(
                          latitude = mViewModel.latitude.get(),
                          longitude = mViewModel.longitude.get(),
                          isProgressBarVisible = false
                      )*/
                } else {

                    //set to never ask again
                    mViewModel.isApiLoading.set(true)
                    mViewModel.callFetchFeedsApi(false, 0.0, 0.0)

                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        shimmerLayout.startShimmerAnimation()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onTryAgainClicked() {
        shimmerLayout.startShimmerAnimation()
        mViewModel.isRecyclerviewVisible.set(false)
        mViewModel.callFetchFeedsApi(
            latitude = mViewModel.latitude.get(),
            longitude = mViewModel.longitude.get()
        )
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

    override fun onPositiveButtonClick(uniqueIdentifier: String) {
        mViewModel.isRecyclerviewVisible.set(true)
        shimmerLayout.startShimmerAnimation()
        mViewModel.callFetchFeedsApi(false, mViewModel.latitude.get(), mViewModel.longitude.get())
    }
}

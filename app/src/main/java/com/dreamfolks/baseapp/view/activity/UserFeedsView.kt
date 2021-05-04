package com.dreamfolks.baseapp.view.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModelProvider
import com.dreamfolks.baseapp.BR
import com.dreamfolks.baseapp.R
import com.dreamfolks.baseapp.base.BaseActivity
import com.dreamfolks.baseapp.databinding.ViewUserFeedsBinding
import com.dreamfolks.baseapp.listener.LocationListenerClass
import com.dreamfolks.baseapp.model.CustomerInfoResponse
import com.dreamfolks.baseapp.model.FeedDetails
import com.dreamfolks.baseapp.util.AppConstants
import com.dreamfolks.baseapp.util.AppConstants.BASE_ACTIVITY_URL
import com.dreamfolks.baseapp.util.AppConstants.FEED_RESPONSE
import com.dreamfolks.baseapp.viewmodel.FeedsViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_user_feeds.*

/*
* This is used to show list of feeds
* */
class UserFeedsView : BaseActivity<ViewUserFeedsBinding, FeedsViewModel>(),
    LocationListenerClass.GetCurrentLocationListener {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()
        setToolbarAndTitle(
            context = this@UserFeedsView,
            toolbar = toolbar,
            textView = toolbar_title,
            toolbarTitle = getString(R.string.feeds_screen_title),
            isBackArrowVisible = true
        )
        LocationListenerClass(
            this@UserFeedsView, this
        ).permissions()
        setObserver()
    }


    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        mViewModel.onFeedButtonClick.observe(this) {
            when (it.action?.type) {
                AppConstants.FEED_TYPE_IN_APP -> {
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
                AppConstants.FEED_TYPE_IN_APP_WEBVIEW, AppConstants.FEED_TYPE_FEED -> {
                    intentToActivity(UserFeedsDetailView::class.java, it, type = it.action?.type)
                }
                AppConstants.FEED_TYPE_EXTERNAL_WEBVIEW -> {
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

        mViewModel.onFeedsApiFail.observe(this) {
            if (it) {
                mViewModel.noDataFoundVisibility.set(true)
                mViewModel.noDataText.set(getString(R.string.something_went_wrong_error1))
                mViewModel.onFeedsApiFail.value = false
            }

        }


    }

    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(aClass: Class<*>, feedDetails: FeedDetails, type: String? = null) {
        val intent = Intent(this@UserFeedsView, aClass)
        intent.putExtra(FEED_RESPONSE, feedDetails)
        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, type)
        intent.putExtra(AppConstants.CUSTOMER_INFO_RESPONSE, CustomerInfoResponse())
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
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                //denied

                if (mViewModel.isDenied.get() == false) {
                    mViewModel.callFetchFeedsApi(false, 0.0, 0.0)
                    mViewModel.isDenied.set(true)
                }
            } else {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        permission
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    //allow
                    if (!isLocationPermissionAllowed.get()!!) {
                        LocationListenerClass(
                            this@UserFeedsView, this
                        ).permissions()
                    }
                    isLocationPermissionAllowed.set(true)
                } else {
                    //set to never ask again
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
        shimmerLayout.stopShimmerAnimation()
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
}
